package com.annoing.dictionary.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annoing.dictionary.domain.User;
import com.annoing.dictionary.domain.WordsSet;

@Repository
public interface WordsSetRepo extends JpaRepository<WordsSet, Long> {

	@Query
	List<WordsSet> findByDefaultSetTrue();

	@Query
	List<WordsSet> findByAuthor(User author);

	@Query
	List<WordsSet> findByTitle(String title);

	@Query(nativeQuery = true, value = "SELECT * FROM words_set WHERE title = :title LIMIT :limit")
	List<WordsSet> findByTitle(@Param("title") String title, @Param("limit") Long limit);

	@Query
	List<WordsSet> findByAuthorAndDefaultSetTrue(User author);
}
