package com.slash.tizilandbot.domain;

import java.util.List;

public record EventQuestion(String question, List<EventQuestionAnswer> answers) {

}
