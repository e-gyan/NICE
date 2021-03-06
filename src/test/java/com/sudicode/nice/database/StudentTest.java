package com.sudicode.nice.database;

import com.sudicode.nice.TestUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link Student}.
 */
public class StudentTest {

    private static AtomicInteger counter;

    private Student student;
    private Course course;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        counter = new AtomicInteger();
        TestUtil.openDbConnection();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        TestUtil.closeDbConnection();
    }

    @Before
    public void setUp() throws Exception {
        student = new Student();
        student.setStudentId(counter.incrementAndGet());
        student.insert();

        course = new Course();
        course.setCrn(counter.incrementAndGet());
        course.insert();
    }

    @Test
    public void testPresent() throws Exception {
        TestUtil.setScheduleForEachDay(course, LocalTime.now().plusMinutes(5), LocalTime.now().plusMinutes(30));
        student.enroll(course);
        student.attend(course);
        assertTrue(student.getStatus(course, LocalDate.now()).startsWith("present"));
    }

    @Test
    public void testLate() throws Exception {
        TestUtil.setScheduleForEachDay(course, LocalTime.of(0, 0), LocalTime.now().plusMinutes(5));
        student.enroll(course);
        student.attend(course);
        assertTrue(student.getStatus(course, LocalDate.now()).startsWith("late"));
    }

    @Test
    public void testNoClass() throws Exception {
        student.enroll(course);
        assertEquals("no class", student.getStatus(course, LocalDate.now()));
    }

    @Test
    public void testAbsent() throws Exception {
        TestUtil.setScheduleForEachDay(course, LocalTime.of(0, 0), LocalTime.of(0, 0));
        student.enroll(course);
        student.attend(course);
        assertEquals("absent", student.getStatus(course, LocalDate.now()));
    }

}
