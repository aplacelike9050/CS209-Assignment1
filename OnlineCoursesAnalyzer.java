package Java2.src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
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
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
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
/*
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
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> result = new TreeMap<>();
        courses.forEach(course -> {
            String[] instructors = course.instructors.split(",");
            for (String instructor : instructors) {
                instructor = instructor.trim();
                if (!result.containsKey(instructor)) {
                    result.put(instructor, new ArrayList<>(Arrays.asList(new ArrayList<>(), new ArrayList<>())));
                }
                if (instructors.length == 1) {
                    result.get(instructor).get(0).add(course.title);
                } else {
                    result.get(instructor).get(1).add(course.title);
                }
            }
        });
        result.forEach((key, value) -> {
            value.get(0).sort(String::compareTo);
            value.get(1).sort(String::compareTo);
        });
        return result;
    }

//4

    public List<String> getCourses(int topK, String by) {
        Comparator<Course> comparator = null;
        switch (by) {
            case "hours" -> comparator = Comparator.comparingDouble((Course course) -> course.totalHours)
                .thenComparing(course -> course.title);
            case "participants" -> comparator = Comparator.comparingInt((Course course) -> course.participants)
                .thenComparing(course -> course.title);
            default -> {
            }
        }

        assert comparator != null;
        return courses.stream()
            .sorted(comparator.reversed())
            .distinct()
            .limit(topK)
            .map(course -> course.title)
            .collect(Collectors.toList());
    }



    //5
       /* public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
            return courses.stream()
                .filter(course -> course.subject.equalsIgnoreCase(courseSubject)
                    && course.percentAudited >= percentAudited
                    && course.totalHours <= totalCourseHours)
                .map(course -> course.title)
                .collect(Collectors.toList());
        }*/

    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        return courses.stream()
            .filter(course -> course.subject.toLowerCase().contains(courseSubject.toLowerCase())
                && course.percentAudited >= percentAudited
                && course.totalHours <= totalCourseHours)
            .sorted(Comparator.comparing(course -> course.title))
            .distinct()
            .map(course -> course.title)
            .collect(Collectors.toList());
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        // Calculate the average Median Age, average % Male, and average % Bachelor's Degree or Higher for each course
        courses.forEach(course -> {
            course.averageMedianAge = course.medianAge + course.averageMedianAge;
            course.averageMale = course.percentMale + course.averageMale;
            course.averageBachelorOrHigher = course.percentDegree + course.averageBachelorOrHigher;
        });


        // Calculate the similarity value for each course
        Map<Course, Double> similarityMap = new HashMap<>();
        courses.forEach(course -> {
            double similarityValue = Math.pow(age - course.averageMedianAge/10, 2)
                + Math.pow(gender * 100 - course.averageMale/10, 2)
                + Math.pow(isBachelorOrHigher * 100 - course.averageBachelorOrHigher/10, 2);
            similarityMap.put(course, similarityValue);
        });

        // Return the top 10 courses with the smallest similarity value
        return courses.stream()
            .distinct()
            .sorted((course1, course2) -> {
                int similarityCompare = similarityMap.get(course1).compareTo(similarityMap.get(course2));
                if (similarityCompare == 0) {
                    return course1.title.compareTo(course2.title);
                }
                return similarityCompare;
            })
            .limit(10)
            .map(course -> course.title)
            .collect(Collectors.toList());
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

    double averageMedianAge;
    double averageMale;
    double averageBachelorOrHigher;
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