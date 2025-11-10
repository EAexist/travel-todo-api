package com.matchalab.trip_todo_api.mapper;
// package com.matchalab.trip_todo_api.mapper;

// @Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
// componentModel = "spring")
// public abstract class FilmMapper {

// protected ReferenceFinderRepository repository;

// @Autowired
// protected void setReferenceFinderRepository(ReferenceFinderRepository
// repository) {
// this.repository = repository;
// }

// public FilmResponse mapToFilmResponse(Film film) {
// return FilmResponse.builder()
// .name(film.getName())
// .description(film.getDescription())
// .duration(film.getDuration())
// .premiereDate(film.getPremiereDate())
// .language(film.getLanguage().getName())
// .mpaa(film.getMpaa().getName())
// .genres(film.getGenres().stream().map(Genre::getName).toList())
// .actors(film.getActors().stream().map(FilmPerson::getName).toList()).build();
// }

// public Film mapToFilm(NewFilmRequest filmRequest) {
// Film.FilmBuilder film = Film.builder()
// .name(filmRequest.name())
// .description(filmRequest.description())
// .premiereDate(filmRequest.premiereDate())
// .duration(filmRequest.duration())
// .mpaa(repository.getMpaaReference(filmRequest.mpaaId()));

// film.genres(filmRequest.genreIds()
// .stream()
// .map(repository::getGenreReference)
// .collect(Collectors.toSet()));

// film.actors(filmRequest.actorIds()
// .stream()
// .map(repository::getFilmPersonReference)
// .collect(Collectors.toSet()));

// double meanRating = 0.0;
// if (!film.getRatings().isEmpty()) {
// meanRating = film.getRatings()
// .stream()
// .map(Rating::getPoints)
// .mapToInt(Integer::intValue)
// .summaryStatistics()
// .getAverage();
// }

// rresponse.rating(round(meanRating, 2));

// return film.build();
// }
// }