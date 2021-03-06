package com.annoing.dictionary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annoing.dictionary.domain.User;
import com.annoing.dictionary.domain.WordsSet;
import com.annoing.dictionary.domain.dto.WordsSetDto;
import com.annoing.dictionary.repo.UserDetailsRepo;
import com.annoing.dictionary.repo.WordsSetRepo;

@Service
public class WordsSetService {
	private WordService wordService;
	private UserService userService;
	private final WordsSetRepo wordsSetRepo;
	private final UserDetailsRepo userRepo;

	@Autowired
	public WordsSetService(WordsSetRepo wordsSetRepo, UserDetailsRepo userRepo) {

		this.wordsSetRepo = wordsSetRepo;

		this.userRepo = userRepo;
	}

	@Autowired
	public void setWordService(WordService wordService) {
		this.wordService = wordService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public List<WordsSet> getAll() {
		return wordsSetRepo.findAll();
	}

	public WordsSet getOne(Long id) {
		return wordsSetRepo.findById(id).get();
	}

	public WordsSetDto getFullOne(Long id) {

		WordsSet set = getOne(id);
		WordsSetDto dto = new WordsSetDto();

		dto.setAuthorName(set.getAuthor().getName());
		dto.setDefaultSet(set.isDefaultSet());
		dto.setDescription(set.getDescription());
		dto.setId(set.getId());
		dto.setTitle(set.getTitle());
		dto.setWords(wordService.getSetWords(set));

		return dto;
	}

	public WordsSet save(WordsSet wordsSet) {
		return wordsSetRepo.save(wordsSet);
	}

	public WordsSet create(WordsSet wordsSet, User author) {
		wordsSet.setAuthor(author);
		wordsSet.setDefaultSet(false);

		return wordsSetRepo.save(wordsSet);
	}

	public WordsSet update(WordsSet afterUpdate, Long id, User author) {
		WordsSet beforeUpdate = getOne(id);

		if (!beforeUpdate.getAuthor().equals(author)) {
			throw new IllegalArgumentException("You are not author of this set!");
		}

		beforeUpdate.setDescription(afterUpdate.getDescription());
		beforeUpdate.setTitle(afterUpdate.getTitle());

		return wordsSetRepo.save(beforeUpdate);
	}

	public void remove(Long id, User author) {
		WordsSet beforeUpdate = getOne(id);

		if (!beforeUpdate.getAuthor().equals(author)) {
			throw new IllegalArgumentException("You are not author of this set!");
		}

		if (!getOne(id).isDefaultSet())
			wordsSetRepo.deleteById(id);
	}

	public WordsSet createDefaultSet(User user) {
		WordsSet defaultSet = new WordsSet();
		defaultSet.setTitle("Default set");
		defaultSet.setDescription("You can save your words here");
		defaultSet.setAuthor(user);

		user.getSets().add(markAsDefault(defaultSet, user));

		userRepo.save(user);

		return defaultSet;
	}

	public WordsSet getDefaultSet(User author) {
		return wordsSetRepo.findByAuthorAndDefaultSetTrue(author).get(0);
	}

	public WordsSet markAsDefault(WordsSet set, User author) {
		if (!set.getAuthor().equals(author)) {
			throw new IllegalArgumentException("You are not author of this set!");
		}

		dropDefaultSet(set.getAuthor());

		set.setDefaultSet(true);

		return wordsSetRepo.save(set);
	}

	public List<WordsSet> getUserSets(String id) {
		User user = userService.getUser(id);
		List<WordsSet> sets = wordsSetRepo.findByAuthor(user);

		if (sets.isEmpty()) {
			sets.add(createDefaultSet(user));
		}

		return sets;
	}

	private void dropDefaultSet(User author) {
		List<WordsSet> defaultSet = wordsSetRepo.findByAuthorAndDefaultSetTrue(author);

		defaultSet.forEach(set -> set.setDefaultSet(false));

		wordsSetRepo.saveAll(defaultSet);
	}

	public List<WordsSet> getSetsByTitle(String title) {
		return wordsSetRepo.findByTitle(title);
	}

	public List<WordsSet> getSetsByTitle(String title, Long limit) {
		if (limit > 0) {
			return wordsSetRepo.findByTitle(title, limit);
		}

		return getSetsByTitle(title);
	}

}
