package poc.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Bet(
        @get:JsonProperty("horse") val horse: String,
        @get:JsonProperty("amount") val amount: Int,
        @get:JsonProperty("timestamp") val timestamp: String
)
