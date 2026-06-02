package com.nunclear.escritores.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exception Handling Tests")
class ExceptionHandlerTest {

    // ============== BadRequestException Tests ==============

    @Test
    @DisplayName("BadRequestException_WithMessage_CreatesException")
    void badRequestExceptionWithMessageCreatesException() {
        // Arrange
        String message = "Invalid input data";

        // Act
        BadRequestException exception = new BadRequestException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("BadRequestException_WithMessageAndCause_CreatesException")
    void badRequestExceptionWithMessageAndCauseCreatesException() {
        // Arrange
        String message = "Invalid input";
        Throwable cause = new IllegalArgumentException("Invalid argument");

        // Act
        BadRequestException exception = new BadRequestException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    // ============== UnauthorizedException Tests ==============

    @Test
    @DisplayName("UnauthorizedException_WithMessage_CreatesException")
    void unauthorizedExceptionWithMessageCreatesException() {
        // Arrange
        String message = "User not authenticated";

        // Act
        UnauthorizedException exception = new UnauthorizedException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    @DisplayName("UnauthorizedException_WithInvalidToken_CreatesException")
    void unauthorizedExceptionWithInvalidTokenCreatesException() {
        // Act
        UnauthorizedException exception = new UnauthorizedException("Invalid or expired token");

        // Assert
        assertEquals("Invalid or expired token", exception.getMessage());
        assertTrue(exception.getMessage().contains("token"));
    }

    // ============== ForbiddenException Tests ==============

    @Test
    @DisplayName("ForbiddenException_WithMessage_CreatesException")
    void forbiddenExceptionWithMessageCreatesException() {
        // Arrange
        String message = "Access denied to resource";

        // Act
        ForbiddenException exception = new ForbiddenException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    // ============== ResourceNotFoundException Tests ==============

    @Test
    @DisplayName("ResourceNotFoundException_WithMessage_CreatesException")
    void resourceNotFoundExceptionWithMessageCreatesException() {
        // Arrange
        String message = "Story not found";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("ResourceNotFoundException_WithResourceType_CreatesException")
    void resourceNotFoundExceptionWithResourceTypeCreatesException() {
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException("User with id 123 not found");

        // Assert
        assertTrue(exception.getMessage().contains("not found"));
    }

    // ============== ConflictException Tests ==============

    @Test
    @DisplayName("ConflictException_WithMessage_CreatesException")
    void conflictExceptionWithMessageCreatesException() {
        // Arrange
        String message = "Email already exists";

        // Act
        ConflictException exception = new ConflictException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("ConflictException_WithDuplicateResource_CreatesException")
    void conflictExceptionWithDuplicateResourceCreatesException() {
        // Act
        ConflictException exception = new ConflictException("Favorite already exists");

        // Assert
        assertTrue(exception.getMessage().contains("already"));
    }

    // ============== ValidationException Tests ==============

    @Test
    @DisplayName("ValidationException_WithMessage_CreatesException")
    void validationExceptionWithMessageCreatesException() {
        // Arrange
        String message = "Validation failed";

        // Act
        ValidationException exception = new ValidationException(message);

        // Assert
        assertEquals(message, exception.getMessage());
    }

    // ============== Exception Inheritance Tests ==============

    @Test
    @DisplayName("AllExceptions_ExtendApplicationException_Successfully")
    void allExceptionsExtendApplicationExceptionSuccessfully() {
        // Arrange
        BadRequestException badRequest = new BadRequestException("test");
        UnauthorizedException unauthorized = new UnauthorizedException("test");
        ResourceNotFoundException notFound = new ResourceNotFoundException("test");

        // Assert
        assertTrue(badRequest instanceof ApplicationException);
        assertTrue(unauthorized instanceof ApplicationException);
        assertTrue(notFound instanceof ApplicationException);
    }

    // ============== Exception Message Tests ==============

    @Test
    @DisplayName("ExceptionMessages_AreConsistent_AcrossTypes")
    void exceptionMessagesAreConsistentAcrossTypes() {
        // Arrange
        String message = "Test error message";

        // Act & Assert
        assertEquals(message, new BadRequestException(message).getMessage());
        assertEquals(message, new UnauthorizedException(message).getMessage());
        assertEquals(message, new ResourceNotFoundException(message).getMessage());
    }

    // ============== HTTP Status Tests ==============

    @Test
    @DisplayName("ExceptionStatuses_MapToCorrectHttpCodes")
    void exceptionStatusesMapToCorrectHttpCodes() {
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, new BadRequestException("test").getStatus());
        assertEquals(HttpStatus.UNAUTHORIZED, new UnauthorizedException("test").getStatus());
        assertEquals(HttpStatus.FORBIDDEN, new ForbiddenException("test").getStatus());
        assertEquals(HttpStatus.NOT_FOUND, new ResourceNotFoundException("test").getStatus());
        assertEquals(HttpStatus.CONFLICT, new ConflictException("test").getStatus());
    }
}
