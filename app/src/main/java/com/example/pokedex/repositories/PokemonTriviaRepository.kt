import com.example.pokedex.shared.Option
import com.example.pokedex.shared.PokemonTriviaModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonTriviaRepository {

    private val answeredQuestions = mutableSetOf<PokemonTriviaModel>()

    private val mutableTriviaFlow = MutableSharedFlow<PokemonTriviaModel?>()
    val triviaFlow: SharedFlow<PokemonTriviaModel?> = mutableTriviaFlow.asSharedFlow()

    private val triviaQuestions = listOf(
        PokemonTriviaModel(
            question = "What is the name of the pokémon, with the ability to morph into any other?",
            options = listOf(
                Option("Swablu", false),
                Option("Pikachu", false),
                Option("Ditto", true),
                Option("Magikarp", false)
            )
        ),
        PokemonTriviaModel(
            question = "Which Pokémon is known as the Fire Lizard?",
            options = listOf(
                Option("Bulbasaur", false),
                Option("Charmander", true),
                Option("Squirtle", false),
                Option("Eevee", false)
            )
        ),
        PokemonTriviaModel(
            question = "What is the evolved form of Pikachu?",
            options = listOf(
                Option("Raichu", true),
                Option("Jolteon", false),
                Option("Zubat", false),
                Option("Electrode", false)
            )
        ),
        PokemonTriviaModel(
            question = "Which Pokemon has the types dragon and ground?",
            options = listOf(
                Option("Pawmo", false),
                Option("Gulpin", false),
                Option("Gabite", true),
                Option("Pheromosa", false)
            )
        ),
        PokemonTriviaModel(
            question = "Which Pokemon has the desc: With quick movements, it chases down its foes, attacking relentlessly with its horns until it prevails",
            options = listOf(
                Option("Rellor", false),
                Option("Scolipede", true),
                Option("Machamp", false),
                Option("Gengar", false)
            )
        ),
        PokemonTriviaModel(
            question = "What Pokemon is a starter pokemon in generation 1",
            options = listOf(
                Option("Pikachu", false),
                Option("Geodude", false),
                Option("Squirtle", true),
                Option("Rattata", false)
            )
        ),
        PokemonTriviaModel(
            question = "What type is the legendary pokemon Moltres",
            options = listOf(
                Option("Ice", false),
                Option("Fire", true),
                Option("Lightning", false),
                Option("Dragon", false)
            )
        ),
    )

    suspend fun loadRandomUnansweredQuestion() {
        val unansweredQuestions = triviaQuestions.filterNot { answeredQuestions.contains(it) }

        if (unansweredQuestions.isEmpty()) {
            println("All questions have been answered.")
            mutableTriviaFlow.emit(null)
        } else {
            val question = unansweredQuestions.random()
            println("Loaded question: ${question.question}")
            mutableTriviaFlow.emit(question)
        }
    }

    suspend fun markQuestionAsAnswered(question: PokemonTriviaModel) {
        answeredQuestions.add(question)
    }

    suspend fun resetQuestions() {
        answeredQuestions.clear()
        println("Questions have been reset.")
        loadRandomUnansweredQuestion()
    }
}