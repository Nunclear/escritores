package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.request.CreateItemRequest;
import com.nunclear.escritores.dto.request.UpdateItemRequest;
import com.nunclear.escritores.dto.response.*;
import com.nunclear.escritores.entity.Item;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.repository.ItemRepository;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.util.PaginationUtils;
import com.nunclear.escritores.util.StoryAccessUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    // La constante de error de historia se comparte mediante StoryAccessUtils.STORY_NOT_FOUND

    private final ItemRepository itemRepository;
    private final StoryRepository storyRepository;
    private final AppUserRepository appUserRepository;

    public CreateItemResponse createItem(CreateItemRequest request) {
        // Utiliza el helper para obtener la historia editable y validar permisos.
        Story story = StoryAccessUtils.getEditableStory(request.storyId(), storyRepository, appUserRepository);

        Item item = new Item();
        item.setStoryId(story.getId());
        item.setName(request.name());
        item.setDescription(request.description());
        item.setQuantity(request.quantity());
        item.setUnitName(request.unitName());

        Item saved = itemRepository.save(item);

        return new CreateItemResponse(
                saved.getId(),
                saved.getStoryId(),
                saved.getName()
        );
    }

    public ItemDetailResponse getItemById(Integer id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado"));

        Story story = storyRepository.findById(item.getStoryId())
                .orElseThrow(() -> new ResourceNotFoundException(StoryAccessUtils.STORY_NOT_FOUND));
        // Valida acceso de lectura usando utilidad compartida
        StoryAccessUtils.validateReadAccess(story, appUserRepository);

        return new ItemDetailResponse(
                item.getId(),
                item.getStoryId(),
                item.getName(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnitName()
        );
    }

    public PageResponse<ItemListItemResponse> getItemsByStory(
            Integer storyId,
            String name,
            int page,
            int size,
            String sort
    ) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException(StoryAccessUtils.STORY_NOT_FOUND));
        // Valida acceso de lectura usando utilidad compartida
        StoryAccessUtils.validateReadAccess(story, appUserRepository);

        Pageable pageable = PaginationUtils.buildPageable(
                page,
                size,
                (sort == null || sort.isBlank() ? "name,asc" : sort),
                this::mapSortField
        );
        Page<Item> result = itemRepository.findByStoryWithNameFilter(storyId, name, pageable);

        return new PageResponse<>(
                result.getContent().stream()
                        .map(item -> new ItemListItemResponse(
                                item.getId(),
                                item.getName()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public UpdateItemResponse updateItem(Integer id, UpdateItemRequest request) {
        Item item = getEditableItem(id);

        item.setName(request.name());
        item.setDescription(request.description());
        item.setQuantity(request.quantity());
        item.setUnitName(request.unitName());

        Item saved = itemRepository.save(item);

        return new UpdateItemResponse(
                saved.getId(),
                saved.getName()
        );
    }

    public MessageResponse deleteItem(Integer id) {
        Item item = getEditableItem(id);
        itemRepository.delete(item);
        return new MessageResponse("Ítem eliminado correctamente");
    }

    private Item getEditableItem(Integer id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado"));
        StoryAccessUtils.getEditableStory(item.getStoryId(), storyRepository, appUserRepository);
        return item;
    }

    // Las funciones de autenticación, autorización y paginación se delegan a las utilidades compartidas.

    private String mapSortField(String field) {
        return switch (field) {
            case "createdAt" -> "createdAt";
            case "updatedAt" -> "updatedAt";
            default -> "name";
        };
    }
}