package com.kbsw.user.dto;

import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.kbsw.user.persistence.entity.Gender;

public record UpdateRequestDTO(
	String school,
	@JsonDeserialize(using = LocalDateDeserializer.class)
	LocalDate birthday,
	String country,
	Gender gender,
	String hobby,
	String about_me,
	String fileName,
	String originalFileName,
	Integer fileSize
) {
}
