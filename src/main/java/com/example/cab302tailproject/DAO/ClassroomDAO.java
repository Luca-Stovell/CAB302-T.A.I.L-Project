package com.example.cab302tailproject.DAO;

import com.example.cab302tailproject.model.Classroom;

import java.util.List;

public interface ClassroomDAO {
    boolean createClassroom(Classroom classroom);

    List<Classroom> getClassroomsByTeacherEmail(String teacherEmail);
}
