package poc.model

data class RaceResult(val id: String = "SOME_RACE_ID", val races: List<Result>)

data class Result(val id: String, val winner: Int)
