package com.fabdev.AlbirLeaks.jobs.DTOs;

// DTO simple para mostrar información del anuncio en la conversación
public record JobSummaryDto(String jobId, String jobTitle, String companyName) {} // Usa el ID de tipo String o Long según tu entidad Job
