package com.hostel.backend.controller;
import com.hostel.backend.dto.FeeRequest;
import com.hostel.backend.entity.FeeRecord;
import com.hostel.backend.service.FeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fees")
public class FeeController {
    private final FeeService service;
    public FeeController(FeeService service) { this.service = service; }
    @GetMapping public List<FeeRecord> all() { return service.all(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public FeeRecord create(@Valid @RequestBody FeeRequest request) { return service.create(request); }
    @PutMapping("/{id}") public FeeRecord update(@PathVariable Long id, @Valid @RequestBody FeeRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}
