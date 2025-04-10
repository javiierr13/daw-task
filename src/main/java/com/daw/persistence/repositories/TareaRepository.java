package com.daw.persistence.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import com.daw.persistence.entities.Tarea;
import com.daw.persistence.entities.enums.Estado;

public interface TareaRepository extends ListCrudRepository<Tarea, Integer> {

	// Obtener el número total de tareas completadas.
	long countByEstado(Estado estado);

	// Obtener tareas por estado.
	List<Tarea> findByEstado(Estado estado);

	// Obtener tareas vencidas.
	List<Tarea> findByFechaVencimientoBefore(LocalDate fecha);

	// Obtener tareas no vencidas.
	List<Tarea> findByFechaVencimientoGreaterThanEqual(LocalDate fecha);

	// Buscar tareas por título que contenga el texto
	List<Tarea> findByTituloContainingIgnoreCase(String titulo);

	// Obtener tareas ordenadas por fecha de vencimiento.
	List<Tarea> findAllByOrderByFechaVencimiento();
}
