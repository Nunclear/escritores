package com.nunclear.escritores.audit;

import com.nunclear.escritores.entity.AuditLog;
import com.nunclear.escritores.repository.AuditLogRepository;
import com.nunclear.escritores.security.CustomUserDetails;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class DatabaseAuditAspect {

    private static final String ACTION_CREATED = "CREATED";
    private static final String ACTION_UPDATED = "UPDATED";
    private static final String ACTION_DELETED = "DELETED";

    private final AuditLogRepository auditLogRepository;

    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.save*(..))")
    public Object auditSave(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        List<EntityState> previousStates = collectEntityStates(args.length > 0 ? args[0] : null);

        Object result = joinPoint.proceed();

        List<Object> savedEntities = collectEntities(result);
        if (savedEntities.isEmpty() && args.length > 0) {
            savedEntities = collectEntities(args[0]);
        }

        for (int i = 0; i < savedEntities.size(); i++) {
            Object entity = savedEntities.get(i);
            if (shouldIgnore(entity)) {
                continue;
            }
            Object previousId = i < previousStates.size() ? previousStates.get(i).id() : null;
            String actionName = Boolean.TRUE.equals(readBoolean(entity, "getDeleted"))
                    ? ACTION_DELETED
                    : previousId == null ? ACTION_CREATED : ACTION_UPDATED;
            saveAuditLog(entity, actionName, "Registro " + actionName.toLowerCase() + " en base de datos");
        }

        return result;
    }

    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.delete*(..))")
    public Object auditDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        List<Object> entities = args.length > 0 ? collectEntities(args[0]) : List.of();
        Object result = joinPoint.proceed();

        if (!entities.isEmpty()) {
            for (Object entity : entities) {
                if (!shouldIgnore(entity)) {
                    saveAuditLog(entity, ACTION_DELETED, "Registro eliminado lógicamente en base de datos");
                }
            }
        } else {
            saveAuditLogForRepository(joinPoint, ACTION_DELETED, args);
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void saveAuditLog(Object entity, String actionName, String description) {
        AuditLog log = new AuditLog();
        log.setActionName(actionName);
        log.setEntityName(entity.getClass().getSimpleName());
        log.setTableName(resolveTableName(entity.getClass()));
        Object id = readId(entity);
        log.setRecordId(id == null ? null : String.valueOf(id));
        fillUser(log);
        fillRequest(log);
        log.setDescription(description);
        log.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void saveAuditLogForRepository(ProceedingJoinPoint joinPoint, String actionName, Object[] args) {
        AuditLog log = new AuditLog();
        log.setActionName(actionName);
        log.setEntityName(joinPoint.getTarget().getClass().getSimpleName());
        log.setTableName(joinPoint.getSignature().getName());
        log.setRecordId(args != null && args.length > 0 && args[0] != null ? String.valueOf(args[0]) : null);
        fillUser(log);
        fillRequest(log);
        log.setDescription("Acción de eliminación ejecutada desde repositorio");
        log.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    private List<EntityState> collectEntityStates(Object source) {
        List<EntityState> states = new ArrayList<>();
        for (Object entity : collectEntities(source)) {
            states.add(new EntityState(readId(entity)));
        }
        return states;
    }

    private List<Object> collectEntities(Object source) {
        if (source == null) {
            return List.of();
        }
        if (source instanceof Iterable<?> iterable) {
            List<Object> list = new ArrayList<>();
            iterable.forEach(list::add);
            return list;
        }
        return List.of(source);
    }

    private boolean shouldIgnore(Object entity) {
        return entity == null || entity instanceof AuditLog || entity.getClass().getName().contains("AuditLog");
    }

    private Object readId(Object entity) {
        try {
            Method method = entity.getClass().getMethod("getId");
            return method.invoke(entity);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Boolean readBoolean(Object entity, String methodName) {
        try {
            Method method = entity.getClass().getMethod(methodName);
            Object value = method.invoke(entity);
            return value instanceof Boolean booleanValue ? booleanValue : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private String resolveTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null && table.name() != null && !table.name().isBlank()) {
            return table.name();
        }
        return entityClass.getSimpleName();
    }

    private void fillUser(AuditLog log) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            log.setUserId(userDetails.getId());
            log.setUsername(userDetails.getUsername());
            return;
        }
        if (authentication.getName() != null && !"anonymousUser".equals(authentication.getName())) {
            log.setUsername(authentication.getName());
        }
    }

    private void fillRequest(AuditLog log) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return;
        }
        var request = servletRequestAttributes.getRequest();
        log.setRequestMethod(request.getMethod());
        log.setRequestPath(request.getRequestURI());
    }

    private record EntityState(Object id) {
    }
}
