package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.StoryRatingDTO;
import com.nunclear.escritores.entity.StoryRating;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.repository.StoryRatingRepository;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StoryRatingService {

    private final StoryRatingRepository ratingRepository;
    private final StoryRepository storyRepository;
    private final AppUserRepository userRepository;

    public StoryRatingDTO rateStory(StoryRatingDTO ratingDTO, Integer userId) {
        Story story = storyRepository.findById(ratingDTO.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));

        if (!story.getAllowScores()) {
            throw new IllegalArgumentException("Ratings are disabled for this story");
        }

        AppUser author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if user already rated this story
        StoryRating existingRating = ratingRepository.findByStoryIdAndAuthorUserId(
                ratingDTO.getStoryId(), userId).orElse(null);

        StoryRating rating = existingRating != null ? existingRating : new StoryRating();
        rating.setStory(story);
        rating.setAuthorUser(author);
        rating.setScoreValue(ratingDTO.getScoreValue());
        rating.setReviewText(ratingDTO.getReviewText());

        StoryRating savedRating = ratingRepository.save(rating);
        return mapToDTO(savedRating);
    }

    public StoryRatingDTO getRatingById(Integer id) {
        return ratingRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found"));
    }

    public List<StoryRatingDTO> getRatingsByStoryId(Integer storyId) {
        return ratingRepository.findByStoryId(storyId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StoryRatingDTO getUserRating(Integer storyId, Integer userId) {
        return ratingRepository.findByStoryIdAndAuthorUserId(storyId, userId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found"));
    }

    public double getAverageRating(Integer storyId) {
        List<StoryRating> ratings = ratingRepository.findByStoryId(storyId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(StoryRating::getScoreValue)
                .average()
                .orElse(0.0);
    }

    public void deleteRating(Integer id, Integer userId) {
        StoryRating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found"));

        if (!rating.getAuthorUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the author of this rating");
        }

        ratingRepository.delete(rating);
    }

    private StoryRatingDTO mapToDTO(StoryRating rating) {
        return StoryRatingDTO.builder()
                .id(rating.getId())
                .storyId(rating.getStory().getId())
                .authorUserId(rating.getAuthorUser().getId())
                .authorUserName(rating.getAuthorUser().getDisplayName())
                .scoreValue(rating.getScoreValue())
                .reviewText(rating.getReviewText())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
}
