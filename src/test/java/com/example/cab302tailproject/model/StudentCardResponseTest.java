package com.example.cab302tailproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentCardResponseTest {

    private static final int responseID = 1;
    private static final int studentID = 2;
    private static final int materialID = 3;
    private static final String cardQuestion = "What do you get when you multiply six by nine?";
    private static final String shortQuestion = "?";
    private static final boolean isCorrect = true;
    private static final int classroomID = 4;

    private static final String response = "StudentCardResponse{" +
            "responseID=" + responseID +
            ", studentID=" + studentID +
            ", materialID=" + materialID +
            ", cardQuestion='" + "What do you get when you multi...'" +
            ", isCorrect=" + isCorrect +
            ", classroomID=" + classroomID +
            '}';
    private static final String shortResponse = "StudentCardResponse{" +
            "responseID=" + responseID +
            ", studentID=" + studentID +
            ", materialID=" + materialID +
            ", cardQuestion='" + "?...'" +
            ", isCorrect=" + isCorrect +
            ", classroomID=" + classroomID +
            '}';

    StudentCardResponse r;
    @BeforeEach
    void setUp() {
        r = new StudentCardResponse(responseID,studentID,materialID,cardQuestion,isCorrect,classroomID);
    }

    @Test
    void getResponseID() {
        assertEquals(responseID, r.getResponseID());
    }

    @Test
    void getStudentID() {
        assertEquals(studentID, r.getStudentID());
    }

    @Test
    void getMaterialID() {
        assertEquals(materialID, r.getMaterialID());
    }

    @Test
    void getCardQuestion() {
        assertEquals(cardQuestion, r.getCardQuestion());
    }

    @Test
    void isCorrect() {
        assertEquals(isCorrect, r.isCorrect());
    }

    @Test
    void getClassroomID() {
        assertEquals(classroomID, r.getClassroomID());
    }

    @Test
    void testToString() {
        assertEquals(response, r.toString());
    }
    @Test
    void testShortToString(){
        r.setCardQuestion(shortQuestion);
        assertEquals(shortResponse, r.toString());
    }
}