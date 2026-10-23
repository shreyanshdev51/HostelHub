package com.hostel.backend.controller;
import com.hostel.backend.dto.AllocationRequest;
import com.hostel.backend.entity.Allocation;
import com.hostel.backend.service.AllocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/allocations")
public class AllocationController {
    private final AllocationService service;
    public AllocationController(AllocationService service) { this.service = service; }
    @GetMapping public List<Allocation> all() { return service.all(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Allocation create(@Valid @RequestBody AllocationRequest request) { return service.create(request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}
