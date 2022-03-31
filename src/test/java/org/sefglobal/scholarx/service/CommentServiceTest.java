package org.sefglobal.scholarx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Comment;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.repository.CommentRepository;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.sefglobal.scholarx.util.ProfileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private final Long menteeId = 1L;
    private final Long profileId = 1L;
    private final Long commentId = 1L;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private MenteeRepository menteeRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    void getAllMenteeCommentsByMenteeId_withAvailableData_thenReturnDataFromRepository()
            throws UnauthorizedException, ResourceNotFoundException {
        Mentee mentee = new Mentee();
        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findById(menteeId);
        Profile profile = new Profile();
        profile.setType(ProfileType.ADMIN);
        doReturn(Optional.of(profile))
                .when(profileRepository)
                .findById(profileId);
        List<Comment> comments = new ArrayList<>();
        doReturn(comments).when(commentRepository)
                .findAllByMenteeId(menteeId);
        List<Comment> returnedData = commentService.getAllMenteeComments(menteeId, profileId);
        assertThat(returnedData).isEqualTo(comments);
    }

    @Test
    void getAllMenteeCommentsByMenteeId__withUnavailableMenteeId_thenThrowResourceNotFoundException() {
        doReturn(Optional.empty())
                .when(menteeRepository)
                .findById(menteeId);
        Profile profile = new Profile();
        profile.setType(ProfileType.ADMIN);
        doReturn(Optional.of(profile))
                .when(profileRepository)
                .findById(profileId);
        Throwable thrown = catchThrowable(
                () -> commentService.getAllMenteeComments(menteeId, profileId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentee by id: 1 doesn't exist.");
    }

    @Test
    void getAllMenteeCommentsByMenteeId__withUnavailableProfileId_thenThrowUnauthorizedException() {
        Mentee mentee = new Mentee();
        Profile profile = new Profile();
        profile.setId(2L);
        Mentor mentor = new Mentor();
        mentor.setProfile(profile);
        mentee.setAssignedMentor(mentor);
        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findById(menteeId);
        profile.setType(ProfileType.DEFAULT);
        doReturn(Optional.of(profile))
                .when(profileRepository)
                .findById(profileId);
        Throwable thrown = catchThrowable(
                () -> commentService.getAllMenteeComments(menteeId, profileId));
        assertThat(thrown)
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Error, User by id: 1 is not allowed access.");
    }


    @Test
    void addMenteeCommentByMenteeIdAndProfileId_withUnavailableMenteeId_thenThrowResourceNotFoundException() {
        doReturn(Optional.empty())
                .when(menteeRepository)
                .findById(menteeId);
        Comment comment = new Comment();
        Throwable thrown = catchThrowable(
                () -> commentService.addMenteeComment(menteeId, profileId, comment));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentee by id: 1 doesn't exist.");
    }

    @Test
    void addMenteeCommentByMenteeIdAndProfileId_withUnavailableProfileId_thenThrowResourceNotFoundException() {
        Mentee mentee = new Mentee();
        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findById(menteeId);
        doReturn(Optional.empty())
                .when(profileRepository)
                .findById(profileId);
        Comment comment = new Comment();
        Throwable thrown = catchThrowable(
                () -> commentService.addMenteeComment(menteeId, profileId, comment));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, User by id: 1 doesn't exist.");
    }

    @Test
    void addMenteeCommentByMenteeIdAndProfileId_withUnavailableProfileId_thenThrowUnauthorizedException() {
        Mentee mentee = new Mentee();
        Profile profile = new Profile();
        profile.setId(2L);
        Mentor mentor = new Mentor();
        mentor.setProfile(profile);
        mentee.setAssignedMentor(mentor);
        Comment comment = new Comment();
        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findById(menteeId);
        profile.setType(ProfileType.DEFAULT);
        doReturn(Optional.of(profile))
                .when(profileRepository)
                .findById(profileId);
        Throwable thrown = catchThrowable(
                () -> commentService.addMenteeComment(menteeId, profileId, comment));
        assertThat(thrown)
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Error, User by id: 1 is not allowed access.");
    }

    @Test
    void updateCommentWithCommentId_withValidData_thenReturnUpdatedData()
            throws UnauthorizedException, ResourceNotFoundException {
        Mentee mentee = new Mentee();
        mentee.setId(menteeId);
        Profile profile = new Profile();
        profile.setId(profileId);
        Comment comment = new Comment();
        comment.setCommented_by(profile);
        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(commentId);

        doReturn(comment)
                .when(commentRepository)
                .save(comment);
        Comment updateComment = commentService.updateComment(commentId, profileId, comment);
        assertThat(updateComment).isNotNull();
    }

    @Test
    void updateCommentWithCommentId_withUnAvailableData_thenThrowResourceNotFoundException() {
        doReturn(Optional.empty())
                .when(commentRepository)
                .findById(commentId);
        Comment comment = new Comment();
        Throwable thrown = catchThrowable(
                () -> commentService.updateComment(commentId, profileId, comment));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Comment with id: 1 cannot be updated. Comment doesn't exist.");
    }

    @Test
    void updateCommentWithCommentId_withUnAvailableData_thenThrowUnauthorizedException() {
        Comment comment = new Comment();
        Profile profile = new Profile();
        profile.setId(profileId);
        comment.setCommented_by(profile);
        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(commentId);
        Throwable thrown = catchThrowable(
                () -> commentService.updateComment(commentId, 2L, comment));
        assertThat(thrown)
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Error, User by id: 2 is not allowed access.");
    }

    @Test
    void deleteComment_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(commentRepository)
                .findById(commentId);
        Throwable thrown = catchThrowable(
                () -> commentService.deleteComment(commentId, profileId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Comment with id: 1 cannot be deleted. Comment doesn't exist.");
    }

    @Test
    void deleteComment_withUnavailableData_thenThrowUnauthorizedException() {
        Comment comment = new Comment();
        Profile profile = new Profile();
        profile.setId(profileId);
        comment.setCommented_by(profile);
        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(commentId);
        Throwable thrown = catchThrowable(
                () -> commentService.deleteComment(commentId, 2L));
        assertThat(thrown)
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Error, User by id: 2 is not allowed access.");
    }
}
