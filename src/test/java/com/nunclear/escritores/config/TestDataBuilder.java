package com.nunclear.escritores.config;

import com.nunclear.escritores.dto.request.LoginRequest;
import com.nunclear.escritores.dto.request.RegisterRequest;
import com.nunclear.escritores.entity.*;
import com.nunclear.escritores.enums.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Builder class for creating test entities and DTOs.
 * Provides factory methods for common test data.
 */
public class TestDataBuilder {

    // ============== DTOs ==============
    
    public static RegisterRequest buildRegisterRequest() {
        return RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Test@123456")
                .build();
    }

    public static RegisterRequest buildRegisterRequest(String username, String email, String password) {
        return RegisterRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
    }

    public static LoginRequest buildLoginRequest() {
        return LoginRequest.builder()
                .email("test@example.com")
                .password("Test@123456")
                .build();
    }

    public static LoginRequest buildLoginRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    // ============== Entities ==============

    public static AppUser buildAppUser() {
        return AppUser.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedPassword123")
                .firstName("Test")
                .lastName("User")
                .profileImage("https://example.com/profile.jpg")
                .bio("Test bio")
                .isActive(true)
                .isBanned(false)
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static AppUser buildAppUser(String username, String email) {
        return AppUser.builder()
                .username(username)
                .email(email)
                .passwordHash("hashedPassword123")
                .firstName("First")
                .lastName("Last")
                .profileImage("https://example.com/profile.jpg")
                .bio("Test bio")
                .isActive(true)
                .isBanned(false)
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static AppUser buildAdminUser() {
        AppUser user = buildAppUser("admin", "admin@example.com");
        user.setRole(UserRole.ADMIN);
        return user;
    }

    public static AppUser buildModeratorUser() {
        AppUser user = buildAppUser("moderator", "moderator@example.com");
        user.setRole(UserRole.MODERATOR);
        return user;
    }

    public static Story buildStory() {
        return Story.builder()
                .author(buildAppUser())
                .title("Test Story")
                .description("A test story description")
                .genre(Genre.FANTASY)
                .visibility(StoryVisibility.PUBLIC)
                .isPublished(true)
                .viewCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Story buildStory(AppUser author, String title) {
        return Story.builder()
                .author(author)
                .title(title)
                .description("Story description for " + title)
                .genre(Genre.FANTASY)
                .visibility(StoryVisibility.PUBLIC)
                .isPublished(true)
                .viewCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Chapter buildChapter(Story story) {
        return Chapter.builder()
                .story(story)
                .chapterNumber(1)
                .title("Chapter 1")
                .content("Chapter content here")
                .isPublished(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Chapter buildChapter(Story story, int chapterNumber, String title) {
        return Chapter.builder()
                .story(story)
                .chapterNumber(chapterNumber)
                .title(title)
                .content("Content for " + title)
                .isPublished(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Character buildCharacter(Story story) {
        return Character.builder()
                .story(story)
                .name("Test Character")
                .description("A test character")
                .role(CharacterRole.PROTAGONIST)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Character buildCharacter(Story story, String name) {
        return Character.builder()
                .story(story)
                .name(name)
                .description("Description of " + name)
                .role(CharacterRole.PROTAGONIST)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Skill buildSkill() {
        return Skill.builder()
                .name("Test Skill")
                .description("A test skill")
                .skillCategory(SkillCategory.MAGIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Skill buildSkill(String name, SkillCategory category) {
        return Skill.builder()
                .name(name)
                .description("Description of " + name)
                .skillCategory(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static CharacterSkill buildCharacterSkill(Character character, Skill skill) {
        return CharacterSkill.builder()
                .character(character)
                .skill(skill)
                .proficiencyLevel(ProficiencyLevel.INTERMEDIATE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Comment buildComment(Story story, AppUser author) {
        return Comment.builder()
                .story(story)
                .author(author)
                .content("Test comment")
                .isHidden(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Comment buildComment(Story story, AppUser author, String content) {
        return Comment.builder()
                .story(story)
                .author(author)
                .content(content)
                .isHidden(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Rating buildRating(Story story, AppUser author, int rating) {
        return Rating.builder()
                .story(story)
                .author(author)
                .rating(rating)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Favorite buildFavorite(AppUser user, Story story) {
        return Favorite.builder()
                .user(user)
                .story(story)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Follow buildFollow(AppUser follower, AppUser followee) {
        return Follow.builder()
                .follower(follower)
                .followee(followee)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Report buildReport(Story story, AppUser reporter) {
        return Report.builder()
                .story(story)
                .reporter(reporter)
                .reason(ReportReason.INAPPROPRIATE_CONTENT)
                .description("Test report")
                .status(ReportStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Event buildEvent() {
        return Event.builder()
                .title("Test Event")
                .description("A test event")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Idea buildIdea(AppUser author) {
        return Idea.builder()
                .author(author)
                .title("Test Idea")
                .description("A test idea")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Item buildItem() {
        return Item.builder()
                .name("Test Item")
                .description("A test item")
                .itemCategory(ItemCategory.WEAPON)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Arc buildArc(Story story) {
        return Arc.builder()
                .story(story)
                .title("Test Arc")
                .description("A test story arc")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Media buildMedia(Story story) {
        return Media.builder()
                .story(story)
                .mediaType(MediaType.IMAGE)
                .mediaUrl("https://example.com/image.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
