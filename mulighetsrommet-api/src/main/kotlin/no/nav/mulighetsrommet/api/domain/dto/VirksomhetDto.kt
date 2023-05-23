package no.nav.mulighetsrommet.api.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class VirksomhetDto(
    val organisasjonsnummer: String,
    val navn: String,
    val overordnetEnhet: String? = null,
    val underenheter: List<VirksomhetDto>? = null,
)