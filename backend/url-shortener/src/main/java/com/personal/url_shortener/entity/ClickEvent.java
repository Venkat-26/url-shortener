package com.personal.url_shortener.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema="public", name = "click_events")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ClickEvent {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	String id;
	
	@Column(name="short_code", nullable=false, length=10)
	String shortCode;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@CreationTimestamp
	@Column(name = "clciked_at", updatable=false)
	private LocalDateTime clickedAt;
	
	@Column(name = "country", length = 100)
	private String country;
	
	@Column(name = "city", length = 100)
	private String city;
	
	@Column(name = "device", length = 50)
	private String device;
	
	@Column(name = "browser", length=50)
	private String browser;
	
	@Column(name = "referrer", length = 2048)
	private String referrer;
	
	@Column(name="ip_address", length=45)
	private String ipAddress;

}
