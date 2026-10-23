package com.hostel.backend.controller;
import com.hostel.backend.dto.RoomRequest;
import com.hostel.backend.entity.Room;
import com.hostel.backend.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService service;
    public RoomController(RoomService service) { this.service = service; }
    @GetMapping public List<Room> all() { return service.all(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Room create(@Valid @RequestBody RoomRequest request) { return service.create(request); }
    @PutMapping("/{id}") public Room update(@PathVariable Long id, @Valid @RequestBody RoomRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}
