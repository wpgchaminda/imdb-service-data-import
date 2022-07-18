package com.imdb.service.dataimport.process;

import com.imdb.service.dataimport.domain.Category;
import com.imdb.service.dataimport.domain.Genre;
import com.imdb.service.dataimport.domain.Job;
import com.imdb.service.dataimport.domain.Person;
import com.imdb.service.dataimport.domain.Title;
import com.imdb.service.dataimport.domain.TitleCrew;
import com.imdb.service.dataimport.domain.TitlePrincipal;
import com.imdb.service.dataimport.domain.TitleType;
import com.imdb.service.dataimport.enums.CrewTypeEnum;
import com.imdb.service.dataimport.service.CategoryService;
import com.imdb.service.dataimport.service.CrewTypeService;
import com.imdb.service.dataimport.service.GenreService;
import com.imdb.service.dataimport.service.JobService;
import com.imdb.service.dataimport.service.PersonService;
import com.imdb.service.dataimport.service.TitleService;
import com.imdb.service.dataimport.service.TitleTypeService;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Log
@Component
public class DataImportProcess {

  @Autowired
  private CrewTypeService crewTypeService;
  @Autowired
  private GenreService genreService;
  @Autowired
  private JobService jobService;
  @Autowired
  private PersonService personService;
  @Autowired
  private TitleService titleService;
  @Autowired
  private TitleTypeService titleTypeService;
  @Autowired
  private CategoryService categoryService;

  @Value("${imdb.data.file.path.title.basics}")
  private String filePathTitleBasics;
  @Value("${imdb.data.file.path.title.principals}")
  private String filePathTitlePrincipals;
  @Value("${imdb.data.file.path.title.crew}")
  private String filePathTitleCrew;
  @Value("${imdb.data.file.path.ratings}")
  private String filePathTitleRatings;

  private final String IMDB_DATAFILE_NULL_CHARACTER = "\\N";

  public void process() {
    try {
      long startTimeMilli = System.currentTimeMillis();
      importData();
      long totalTimeMilli = System.currentTimeMillis() - startTimeMilli;
      log.info("TOTAL TIME: " + totalTimeMilli + "ms");
    } catch (Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  private void importData() throws Exception {
    //title.basics.tsv
    processTitleBasics();

    //title.principals.tsv
    processTitlePrincipals();

    //title.crew.tsv
    processTitleCrew();

    //title.ratings.tsv
    processTitleRatings();
  }

  private void processTitleBasics() throws Exception {
    try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePathTitleBasics))) {
      String line;
      //Skip Header Line
      bufferedReader.readLine();

      long count = 0;
      while ((line = bufferedReader.readLine()) != null) {
        count++;
        if (count > 200) {
          break;
        }
        //Split the Line
        final List<String> data = Arrays.asList(line.split("\t"));

        if (data != null && data.size() > 0) {

          if (StringUtils.hasText(data.get(0)) && StringUtils.hasText(data.get(2))) {
            Title title = new Title();
            //Id
            title.setId(data.get(0).trim());

            //TitleType
            TitleType titleType = null;
            if (StringUtils.hasText(data.get(1)) && !data.get(1).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
              if (titleTypeService.existByName(data.get(1).trim())) {
                titleType = titleTypeService.findByName(data.get(1).trim());
              } else {
                titleType = new TitleType();
                titleType.setName(data.get(1).trim().toUpperCase());
                titleTypeService.save(titleType);
              }
            }
            title.setTitleType(titleType);

            //PrimaryTitle
            title.setPrimaryTitle(data.get(2).trim());

            //OriginalTitle
            String originalTitle = null;
            if (StringUtils.hasText(data.get(3)) && !data.get(3).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
              originalTitle = data.get(3).trim();
            }
            title.setOriginalTitle(originalTitle);

            //IsAdult
            boolean isAdult = false;
            try {
              if (StringUtils.hasText(data.get(4)) && !data.get(4).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                isAdult = Boolean.parseBoolean(data.get(4).trim());
              }
            } catch (Exception ex) {
              isAdult = false;
            }
            title.setIsAdult(isAdult);

            //StartYear
            Integer startYear = null;
            try {
              if (StringUtils.hasText(data.get(5)) && !data.get(5).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                startYear = Integer.parseInt(data.get(5).trim());
              }
            } catch (Exception ex) {
              startYear = null;
            }
            title.setStartYear(startYear);

            //EndYear
            Integer endYear = null;
            try {
              if (StringUtils.hasText(data.get(6)) && !data.get(6).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                endYear = Integer.parseInt(data.get(6).trim());
              }
            } catch (Exception ex) {
              endYear = null;
            }
            title.setEndYear(endYear);

            //RuntimeMinutes
            Integer runtimeMinutes = null;
            try {
              if (StringUtils.hasText(data.get(7)) && !data.get(7).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                runtimeMinutes = Integer.parseInt(data.get(7).trim());
              }
            } catch (Exception ex) {
              runtimeMinutes = null;
            }
            title.setRuntimeMinutes(runtimeMinutes);

            //Genres
            Set<Genre> genres = new HashSet<>();
            if (StringUtils.hasText(data.get(8)) && !data.get(8).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {

              List<String> genresList = Arrays.asList(data.get(8).trim().split(","));
              if (genresList != null && genresList.size() > 0) {
                for (String genreStr : genresList) {
                  if (StringUtils.hasText(genreStr)) {
                    if (genreService.existByName(genreStr.trim())) {
                      Genre genre = genreService.findByName(genreStr.trim());
                      genres.add(genre);
                    } else {
                      Genre genre = new Genre();
                      genre.setName(genreStr.trim().toUpperCase());
                      genreService.save(genre);
                      genres.add(genre);
                    }
                  }
                }
              }
            }
            title.setGenres(genres.size() > 0 ? genres : null);

            //Save Title
            titleService.save(title);
            log.info("SUCCESS, Line: " + count);
          } else {
            log.info("FAILED, Required information not found in line: " + count);
            continue;
          }
        }
      }
      log.info("RECORDS COUNT: " + count);
    } catch (Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      throw e;
    }
  }

  private void processTitlePrincipals() throws Exception {
    try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePathTitlePrincipals))) {
      String line;
      //Skip Header Line
      bufferedReader.readLine();

      long count = 0;
      while ((line = bufferedReader.readLine()) != null) {
        count++;
        if (count > 2000) {
          break;
        }

        //Split the Line
        final List<String> data = Arrays.asList(line.split("\t"));

        if (data != null && data.size() > 0) {

          if (StringUtils.hasText(data.get(0))
              && StringUtils.hasText(data.get(1))
              && StringUtils.hasText(data.get(2))) {

            String titleId = data.get(0).trim();
            Title title = titleService.findById(titleId);

            if (title != null) {
              log.info("Count:" + count + " TITLE:" + title.getId());
              Set<TitlePrincipal> titlePrincipals = title.getTitlePrincipals();
              TitlePrincipal titlePrincipal = new TitlePrincipal();

              //Title
              titlePrincipal.setTitle(title);

              //Ordering
              Integer ordering = null;
              try {
                ordering = Integer.parseInt(data.get(1).trim());
              } catch (Exception ex) {
                ordering = null;
              }

              if (ordering != null) {
                titlePrincipal.setOrderId(ordering);
              } else {
                log.info("FAILED, Ordering  is invalid in line: " + count);
                continue;
              }

              //nconst
              Person person = null;
              if (StringUtils.hasText(data.get(2)) && !data.get(2).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                person = personService.findById(data.get(2).trim());
              }

              if (person != null) {
                titlePrincipal.setPerson(person);
              } else {
                log.info("FAILED, nconst  is invalid in line: " + count);
                continue;
              }

              //category
              Category category = null;
              if (StringUtils.hasText(data.get(3)) && !data.get(3).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                if (categoryService.existByName(data.get(3).trim())) {
                  category = categoryService.findByName(data.get(3).trim());
                } else {
                  category = new Category();
                  category.setName(data.get(3).trim().toUpperCase());
                  categoryService.save(category);
                }
              }
              titlePrincipal.setCategory(category);

              //job
              Job job = null;
              if (StringUtils.hasText(data.get(4)) && !data.get(4).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                if (jobService.existByName(data.get(4).trim())) {
                  job = jobService.findByName(data.get(4).trim());
                } else {
                  job = new Job();
                  job.setName(data.get(4).trim().toUpperCase());
                  jobService.save(job);
                }
              }
              titlePrincipal.setJob(job);

              //characters
              String characters = null;
              if (StringUtils.hasText(data.get(5)) && !data.get(5).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                characters = data.get(5).trim();
              }
              titlePrincipal.setCharacters(characters);

              //Save
              if (titlePrincipals == null) {
                titlePrincipals = new HashSet<>();
              }

              log.info("TitlePrinciple:" + titlePrincipal.getTitle().getId());
              titlePrincipals.add(titlePrincipal);
              title.setTitlePrincipals(titlePrincipals);
              titleService.save(title);

              log.info("SUCCESS, Line: " + count);
            } else {
              log.info("FAILED, Title not found in line: " + count);
              continue;
            }
          } else {
            log.info("FAILED, Required information not found in line: " + count);
            continue;
          }
        }
      }
      log.info("RECORDS COUNT: " + count);
    } catch (Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      throw e;
    }
  }

  private void processTitleCrew() throws Exception {
    try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePathTitleCrew))) {
      String line;
      //Skip Header Line
      bufferedReader.readLine();

      long count = 0;
      while ((line = bufferedReader.readLine()) != null) {
        count++;
        if (count > 300) {
          break;
        }
        //Split the Line
        final List<String> data = Arrays.asList(line.split("\t"));

        if (data != null && data.size() > 0) {

          if (StringUtils.hasText(data.get(0))) {

            String titleId = data.get(0).trim();
            Title title = titleService.findById(titleId);
            if (title != null) {

              Set<TitleCrew> titleCrews = title.getTitleCrews();
              if (titleCrews == null) {
                titleCrews = new HashSet<>();
              }

              //Directors
              String directorsStr = data.get(1);
              if (StringUtils.hasText(directorsStr) && !directorsStr.trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                List<String> directors = Arrays.asList(directorsStr.trim().split(","));
                if (directors != null && directors.size() > 0) {
                  for (String director : directors) {
                    if (StringUtils.hasText(director)) {
                      Person person = personService.findById(director);
                      if (person != null) {
                        TitleCrew titleCrew = new TitleCrew();
                        titleCrew.setTitle(title);
                        titleCrew.setPerson(person);
                        titleCrew.setCrewType(crewTypeService.findByName(CrewTypeEnum.DIRECTOR.name()));
                        titleCrews.add(titleCrew);
                      }
                    }
                  }
                }
              }

              //Writers
              String writersStr = data.get(2);
              if (StringUtils.hasText(writersStr) && !writersStr.trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                List<String> writers = Arrays.asList(writersStr.trim().split(","));
                if (writers != null && writers.size() > 0) {
                  for (String writer : writers) {
                    if (StringUtils.hasText(writer)) {
                      Person person = personService.findById(writer);
                      if (person != null) {
                        TitleCrew titleCrew = new TitleCrew();
                        titleCrew.setTitle(title);
                        titleCrew.setPerson(person);
                        titleCrew.setCrewType(crewTypeService.findByName(CrewTypeEnum.WRITER.name()));
                        titleCrews.add(titleCrew);
                      }
                    }
                  }
                }
              }

              //Save
              title.setTitleCrews(titleCrews);
              titleService.save(title);

              log.info("SUCCESS, Line: " + count);
            } else {
              log.info("FAILED, Title not found in line: " + count);
              continue;
            }
          } else {
            log.info("FAILED, Required information not found in line: " + count);
            continue;
          }
        }
      }
      log.info("RECORDS COUNT: " + count);
    } catch (Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      throw e;
    }
  }

  private void processTitleRatings() throws Exception {
    try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePathTitleRatings))) {
      String line;
      //Skip Header Line
      bufferedReader.readLine();

      long count = 0;
      while ((line = bufferedReader.readLine()) != null) {
        count++;
        /*if (count > 300) {
          break;
        }*/
        //Split the Line
        final List<String> data = Arrays.asList(line.split("\t"));

        if (data != null && data.size() > 0) {

          if (StringUtils.hasText(data.get(0))) {

            String titleId = data.get(0).trim();
            Title title = titleService.findById(titleId);
            if (title != null) {

              //Rating
              BigDecimal rating = null;
              try {
                if (StringUtils.hasText(data.get(1)) && !data.get(1).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                  rating = new BigDecimal(data.get(1).trim());
                }
              } catch (Exception ex) {
              }
              title.setAverageRating(rating);

              //Votes
              Integer votes = null;
              try {
                if (StringUtils.hasText(data.get(2)) && !data.get(2).trim().equals(IMDB_DATAFILE_NULL_CHARACTER)) {
                  votes = Integer.parseInt(data.get(2).trim());
                }
              } catch (Exception ex) {
              }
              title.setNumVotes(votes);

              //Save
              titleService.save(title);

              log.info("SUCCESS, Line: " + count);
            } else {
              log.info("FAILED, Title not found in line: " + count);
              continue;
            }
          } else {
            log.info("FAILED, Required information not found in line: " + count);
            continue;
          }
        }
      }
      log.info("RECORDS COUNT: " + count);
    } catch (Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      throw e;
    }
  }

}
