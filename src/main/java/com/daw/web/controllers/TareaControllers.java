package com.daw.web.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.persistence.entities.Tarea;
import com.daw.persistence.entities.enums.Estado;
import com.daw.services.dto.TareaService;
import com.daw.services.exception.TareaException;
import com.daw.services.exception.TareaNotFoundException;

@RestController
@RequestMapping("/tareas")
public class TareaControllers {

	@Autowired
	private TareaService tareaService;

	@GetMapping
	public ResponseEntity<List<Tarea>> list() {

		return ResponseEntity.status(HttpStatus.OK).body(this.tareaService.findALL());

	}

	@GetMapping("/{idTarea}")
	public ResponseEntity<?> findById(@PathVariable int idTarea) {
		try {
			return ResponseEntity.ok(this.tareaService.findById(idTarea));
		} catch (TareaNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

		}
	}

	@DeleteMapping("/{idTarea}")
	public ResponseEntity<?> delete(@PathVariable int idTarea) {
//		if (this.tareaService.deleteById(idTarea)) {
//			return ResponseEntity.status(HttpStatus.OK).build();
//		} else {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El ID indicado no existe");
//
//		}
		try {
			this.tareaService.deleteById(idTarea);
			return ResponseEntity.ok("La tarea a sido borrada correctamente");
		} catch (TareaNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<Tarea> create(@RequestBody Tarea tarea) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.tareaService.create(tarea));
	}

	@PutMapping("/{idTarea}")
	public ResponseEntity<?> update(@PathVariable int idTarea, @RequestBody Tarea tarea) {

		try {
			return ResponseEntity.ok(this.tareaService.update(idTarea, tarea));
		} catch (TareaNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		} catch (TareaException ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
		}
	}

	// Iniciar tarea
	@PutMapping("/iniciar/{idTarea}")
	public ResponseEntity<?> iniciarTarea(@PathVariable int idTarea) {
		try {
			return ResponseEntity.ok(this.tareaService.iniciarTarea(idTarea));
		} catch (TareaException | TareaNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
		}
	}

	// Completar tarea
	@PutMapping("/completar/{idTarea}")
	public ResponseEntity<?> completarTarea(@PathVariable int idTarea) {
		try {
			return ResponseEntity.ok(this.tareaService.completarTarea(idTarea));
		} catch (TareaException | TareaNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
		}
	}

	// Obtener tareas por estado
	@GetMapping("/pendientes")
	public ResponseEntity<List<Tarea>> tareasPendientes() {
		return ResponseEntity.ok(this.tareaService.findByEstado(Estado.PENDIENTE));
	}

	@GetMapping("/progreso")
	public ResponseEntity<List<Tarea>> tareasEnProgreso() {
		return ResponseEntity.ok(this.tareaService.findByEstado(Estado.EN_PROGRESO));
	}

	@GetMapping("/completadas")
	public ResponseEntity<List<Tarea>> tareasCompletadas() {
		return ResponseEntity.ok(this.tareaService.findByEstado(Estado.COMPLETADA));
	}

	// Obtener tareas vencidas
	@GetMapping("/vencidas")
	public ResponseEntity<List<Tarea>> tareasVencidas() {
		return ResponseEntity.ok(this.tareaService.tareasVencidas());
	}

	// Obtener tareas no vencidas
	@GetMapping("/no-vencidas")
	public ResponseEntity<List<Tarea>> tareasNoVencidas() {
		return ResponseEntity.ok(this.tareaService.tareasNoVencidas());
	}

	// Buscar por título
	@GetMapping("/buscar/{titulo}")
	public ResponseEntity<List<Tarea>> buscarPorTitulo(@PathVariable String titulo) {
		return ResponseEntity.ok(this.tareaService.buscarPorTitulo(titulo));
	}

}
