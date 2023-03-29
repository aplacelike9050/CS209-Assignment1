package Java2.src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is just a demo for you, please run it on
 * JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {
  List<Course> courses = new ArrayList<>();

  public OnlineCoursesAnalyzer(String datasetPath) {
    BufferedReader br = null;
    String line;
    try {
      br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
        Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
            Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
            Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
            Double.parseDouble(info[12]), Double.parseDouble(info[13]),
            Double.parseDouble(info[14]), Double.parseDouble(info[15]),
            Double.parseDouble(info[16]), Double.parseDouble(info[17]),
            Double.parseDouble(info[18]), Double.parseDouble(info[19]),
            Double.parseDouble(info[20]), Double.parseDouble(info[21]),
            Double.parseDouble(info[22]));
        courses.add(course);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //1
  public Map<String, Integer> getPtcpCountByInst() {
    Map<String, Integer> result = new TreeMap<>();
    courses.forEach(course -> result.put(course.institution, result.getOrDefault(course.institution, 0) + course.participants));
    return result;
  }

  //2
  public Map<String, Integer> getPtcpCountByInstAndSubject() {
    Map<String, Integer> unsortedMap = new HashMap<>();
    courses.forEach(course -> {
      String key = course.institution + "-" + course.subject;
      unsortedMap.put(key, unsortedMap.getOrDefault(key, 0) + course.participants);
    });
    return unsortedMap.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
            .thenComparing(Map.Entry.comparingByKey()))
        .collect
            (Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  //3

  public Map<String, List<List<String>>> getCourseListOfInstructor() {
    Map<String, List<List<String>>> tempResult = new HashMap<>();
    courses.forEach(course -> {
      String[] instructors = course.instructors.split(",");
      for (String instructor : instructors) {
        instructor = instructor.trim();
        if (!tempResult.containsKey(instructor)) {
          tempResult.put(instructor,
              Arrays.asList(new ArrayList<String>(), new ArrayList<String>()));
        }
        if (instructors.length == 1) {
          tempResult.get(instructor).get(0).add(course.title);
        } else {
          tempResult.get(instructor).get(1).add(course.title);
        }
      }
    });

    // Convert List<String> to sorted List<String>, and remove duplicates
    Map<String, List<List<String>>> result = new LinkedHashMap<>();
    tempResult.forEach((instructor, courseLists) -> {
      List<List<String>> sortedCourseLists = new ArrayList<>();
      courseLists.forEach(courseList -> {
        List<String> sortedCourses = new ArrayList<>(new HashSet<>(courseList));
        Collections.sort(sortedCourses);
        sortedCourseLists.add(sortedCourses);
      });
      result.put(instructor, sortedCourseLists);
    });

    return result;
  }


  //4

  public List<String> getCourses(int topK, String by) {
    Comparator<Course> comparator = null;
    switch (by) {
      case "hours" -> comparator =
          Comparator.comparingDouble((Course course) -> course.totalHours)
          .thenComparing(course -> course.title);
      case "participants" -> comparator =
          Comparator.comparingInt((Course course) -> course.participants)
          .thenComparing(course -> course.title);
      default -> {
      }
    }

    assert comparator != null;
    return courses.stream()
        .sorted(comparator.reversed())
        .map(course -> course.title)
        .distinct()
        .limit(topK)
        .collect(Collectors.toList());
  }


  //5
  public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
    return courses.stream()
        .filter(course -> course.subject.toLowerCase().contains(courseSubject.toLowerCase())
            && course.percentAudited >= percentAudited
            && course.totalHours <= totalCourseHours)
        .sorted(Comparator.comparing(course -> course.title))
        .map(course -> course.title)
        .collect(Collectors.toSet())
        .stream()
        .sorted()
        .collect(Collectors.toList());
  }


  //6
  public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
    return null;
  }


}


class Course {
  String institution;
  String number;
  Date launchDate;
  String title;
  String instructors;
  String subject;
  int year;
  int honorCode;
  int participants;
  int audited;
  int certified;
  double percentAudited;
  double percentCertified;
  double percentCertified50;
  double percentVideo;
  double percentForum;
  double gradeHigherZero;
  double totalHours;
  double medianHoursCertification;
  double medianAge;
  double percentMale;
  double percentFemale;
  double percentDegree;


  public Course(String institution, String number, Date launchDate,
                String title, String instructors, String subject,
                int year, int honorCode, int participants,
                int audited, int certified, double percentAudited,
                double percentCertified, double percentCertified50,
                double percentVideo, double percentForum, double gradeHigherZero,
                double totalHours, double medianHoursCertification,
                double medianAge, double percentMale, double percentFemale,
                double percentDegree) {
    this.institution = institution;
    this.number = number;
    this.launchDate = launchDate;
    if (title.startsWith("\"")) title = title.substring(1);
    if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
    this.title = title;
    if (instructors.startsWith("\"")) instructors = instructors.substring(1);
    if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
    this.instructors = instructors;
    if (subject.startsWith("\"")) subject = subject.substring(1);
    if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
    this.subject = subject;
    this.year = year;
    this.honorCode = honorCode;
    this.participants = participants;
    this.audited = audited;
    this.certified = certified;
    this.percentAudited = percentAudited;
    this.percentCertified = percentCertified;
    this.percentCertified50 = percentCertified50;
    this.percentVideo = percentVideo;
    this.percentForum = percentForum;
    this.gradeHigherZero = gradeHigherZero;
    this.totalHours = totalHours;
    this.medianHoursCertification = medianHoursCertification;
    this.medianAge = medianAge;
    this.percentMale = percentMale;
    this.percentFemale = percentFemale;
    this.percentDegree = percentDegree;
  }
}