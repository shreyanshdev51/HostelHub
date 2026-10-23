package com.hostel.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 30)
    private String registrationNumber;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String phone;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String course;

    @Size(max = 255)
    private String address;

    public Student() {}

    public Student(String registrationNumber, String name, String email, String phone, String course, String address) {
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.course = course;
        this.address = address;
    }

    public Long getId() { return id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String value) { this.registrationNumber = value; }
    public String getName() { return name; }
    public void setName(String value) { this.name = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }
    public String getPhone() { return phone; }
    public void setPhone(String value) { this.phone = value; }
    public String getCourse() { return course; }
    public void setCourse(String value) { this.course = value; }
    public String getAddress() { return address; }
    public void setAddress(String value) { this.address = value; }
}
