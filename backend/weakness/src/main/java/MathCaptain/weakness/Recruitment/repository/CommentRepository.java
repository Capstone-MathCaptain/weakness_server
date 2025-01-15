package MathCaptain.weakness.Recruitment.repository;

import MathCaptain.weakness.Recruitment.domain.Comment;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Recruitment post);
}
