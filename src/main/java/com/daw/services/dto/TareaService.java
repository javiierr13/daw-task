package com.daw.services.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daw.persistence.entities.Tarea;
import com.daw.persistence.entities.enums.Estado;
import com.daw.persistence.repositories.TareaRepository;
import com.daw.services.exception.TareaException;
import com.daw.services.exception.TareaNotFoundException;

@Service
public class TareaService {

	@Autowired
	private TareaRepository tareaRepository;

	public List<Tarea> findALL() {
		return this.tareaRepository.findAll();
	}

	public Tarea findById(int idTarea) {
		if (!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("No existe tarea con el ID" + idTarea);
		}

		return this.tareaRepository.findById(idTarea).get();
	}

	public boolean existsById(int idTarea) {
		return this.tareaRepository.existsById(idTarea);
	}

	public void deleteById(int idTarea) {
		if (!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("No existe tarea con el ID" + idTarea);
		}

		this.tareaRepository.deleteById(idTarea);

	}

	public Tarea create(Tarea tarea) {
		tarea.setEstado(Estado.PENDIENTE);
		tarea.setFechaCreacion(LocalDate.now());

		if (tarea.getFechaVencimiento() != null && !tarea.getFechaVencimiento().isAfter(tarea.getFechaCreacion())) {
			throw new TareaException("La fecha de vencimiento debe ser posterior a la fecha de creación.");
		}

		return this.tareaRepository.save(tarea);
	}

	public Tarea update(int idTarea, Tarea tarea) {
		if (idTarea != tarea.getId()) {
			throw new TareaException(
					"El ID del Path (" + idTarea + ") y el del body (" + tarea.getId() + ") no coinciden");
		}
		if (!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("No existe tarea con el ID " + idTarea);
		}
		if (tarea.getFechaCreacion() != null || tarea.getEstado() != null) {
			throw new TareaException("No se permite modificar la fecha de creación y/o el estado");
		}

		Tarea tareaBD = this.findById(tarea.getId());

		tareaBD.setTitulo(tarea.getTitulo());
		tareaBD.setDescripcion(tarea.getDescripcion());
		tareaBD.setFechaVencimiento(tarea.getFechaVencimiento());

		if (tareaBD.getFechaVencimiento() != null
				&& !tareaBD.getFechaVencimiento().isAfter(tareaBD.getFechaCreacion())) {
			throw new TareaException("La fecha de vencimiento debe ser posterior a la fecha de creación.");
		}

		return this.tareaRepository.save(tareaBD);
	}

	// Ejemplos Optional
	public boolean deleteDeclarativo(int idTarea) {
		boolean result = false;

		if (this.tareaRepository.existsById(idTarea)) {
			this.tareaRepository.deleteById(idTarea);
			result = true;
		}

		return result;
	}

	public boolean deleteFuncional(int idTarea) {
		return this.tareaRepository.findById(idTarea).map(t -> {
			this.tareaRepository.deleteById(idTarea);
			return true;
		}).orElse(false);
	}

	public Tarea findByIdFuncional(int idTarea) {
		return this.tareaRepository.findById(idTarea)
				.orElseThrow(() -> new TareaNotFoundException("No existe la tarea con ID: " + idTarea));
	}

	// Ejemplos Stream
	// Obtener el número total de tareas completadas.
	public long totalTareasCompletadasFuncional() {
		return this.tareaRepository.findAll().stream().filter(t -> t.getEstado() == Estado.COMPLETADA).count();
	}

	public long totalTareasCompletadas() {
		return this.tareaRepository.countByEstado(Estado.COMPLETADA);
	}

	// Obtener una lista de las fechas de vencimiento de las tareas que estén en
	// progreso
	public List<LocalDate> fechasVencimientoEnProgresoFuncional() {
		return this.tareaRepository.findAll().stream().filter(t -> t.getEstado() == Estado.EN_PROGRESO)
				.map(t -> t.getFechaVencimiento()).collect(Collectors.toList());
	}

	public List<LocalDate> fechasVencimientoEnProgreso() {
		return this.tareaRepository.findByEstado(Estado.EN_PROGRESO).stream().map(t -> t.getFechaVencimiento())
				.collect(Collectors.toList());
	}

	// Obtener las tareas vencidas
	public List<Tarea> tareasVencidasFuncional() {
		return this.tareaRepository.findAll().stream().filter(t -> t.getFechaVencimiento().isBefore(LocalDate.now()))
				.collect(Collectors.toList());
	}

	public List<Tarea> tareasVencidas() {
		return this.tareaRepository.findByFechaVencimientoBefore(LocalDate.now());
	}

	// Obtener los títulos de las tareas pendientes
	public List<String> titulosPendientesFuncional() {
		return this.tareaRepository.findAll().stream().filter(t -> t.getEstado() == Estado.PENDIENTE)
				.map(t -> t.getTitulo()).collect(Collectors.toList());
	}

	public List<String> titulosPendientes() {
		return this.tareaRepository.findByEstado(Estado.PENDIENTE).stream().map(t -> t.getTitulo())
				.collect(Collectors.toList());
	}

	// Obtener las tareas ordenadas por fecha de vencimiento
	public List<Tarea> ordenadasFechaVencimientoFuncional() {
		return this.tareaRepository.findAll().stream()
				.sorted((t1, t2) -> t1.getFechaVencimiento().compareTo(t2.getFechaVencimiento()))
				.collect(Collectors.toList());
	}

	public List<Tarea> ordenadasFechaVencimiento() {
		return this.tareaRepository.findAllByOrderByFechaVencimiento();
	}

	// Iniciar una tarea
	public Tarea iniciarTarea(int idTarea) {
		Tarea tarea = this.findByIdFuncional(idTarea);
		if (tarea.getEstado() != Estado.PENDIENTE) {
			throw new TareaException("Solo se pueden iniciar tareas que están PENDIENTES");
		}
		tarea.setEstado(Estado.EN_PROGRESO);
		return this.tareaRepository.save(tarea);
	}

	// Completar una tarea
	public Tarea completarTarea(int idTarea) {
		Tarea tarea = this.findByIdFuncional(idTarea);
		if (tarea.getEstado() != Estado.EN_PROGRESO) {
			throw new TareaException("Solo se pueden completar tareas que están EN_PROGRESO");
		}
		tarea.setEstado(Estado.COMPLETADA);
		return this.tareaRepository.save(tarea);
	}

	public List<Tarea> findByEstado(Estado estado) {
		return this.tareaRepository.findByEstado(estado);
	}

	// Tareas NO vencidas
	public List<Tarea> tareasNoVencidas() {
		return this.tareaRepository.findByFechaVencimientoGreaterThanEqual(LocalDate.now());
	}

	// Buscar por título
	public List<Tarea> buscarPorTitulo(String titulo) {
		return this.tareaRepository.findByTituloContainingIgnoreCase(titulo);
	}

}