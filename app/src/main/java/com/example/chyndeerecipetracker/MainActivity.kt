package com.chyndee.chyndeerecipetracker

import android.Manifest
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chyndee.chyndeerecipetracker.domain.model.CookingEntry
import com.chyndee.chyndeerecipetracker.domain.model.DietaryTag
import com.chyndee.chyndeerecipetracker.domain.model.Recipe
import com.chyndee.chyndeerecipetracker.domain.model.SortOption
import com.chyndee.chyndeerecipetracker.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlin.collections.minus
import kotlin.collections.plus
import kotlin.collections.toList
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Delete
import java.util.*



fun generateAIRecipe(
    ingredients: String,
    maxTime: Int,
    servings: Int,
    dietaryTags: List<DietaryTag>
): Recipe {
    val ingredientList = ingredients.split(",").map { it.trim() }

    // Simple AI logic based on ingredients
    val recipeName = when {
        ingredientList.any { it.contains("chicken", true) } &&
                ingredientList.any { it.contains("rice", true) } -> "AI Chicken Fried Rice"

        ingredientList.any { it.contains("pasta", true) } &&
                ingredientList.any { it.contains("tomato", true) } -> "AI Mediterranean Pasta"

        ingredientList.any { it.contains("egg", true) } -> "AI Veggie Scramble Bowl"

        ingredientList.any { it.contains("potato", true) } -> "AI Roasted Potato Medley"

        else -> "AI Custom ${ingredientList.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Mystery"} Dish"
    }

    val recipeIngredients = when {
        recipeName.contains("Chicken Fried Rice") -> listOf(
            "2 cups cooked rice", "1 lb chicken breast, diced", "2 eggs, beaten",
            "1 onion, chopped", "2 cloves garlic, minced", "2 tbsp soy sauce",
            "1 tbsp sesame oil", "2 green onions, sliced", "Salt and pepper to taste"
        )
        recipeName.contains("Mediterranean Pasta") -> listOf(
            "12 oz pasta", "2 cups cherry tomatoes, halved", "1/4 cup olive oil",
            "3 cloves garlic, minced", "1/2 cup kalamata olives", "1/4 cup fresh basil",
            "1/2 cup feta cheese, crumbled", "Salt and pepper to taste"
        )
        recipeName.contains("Veggie Scramble") -> listOf(
            "6 eggs", "1 bell pepper, diced", "1 onion, chopped",
            "2 cups spinach", "1/2 cup cheese, shredded", "2 tbsp olive oil",
            "Salt and pepper to taste", "Fresh herbs for garnish"
        )
        recipeName.contains("Roasted Potato") -> listOf(
            "2 lbs potatoes, cubed", "3 tbsp olive oil", "1 tsp garlic powder",
            "1 tsp paprika", "1/2 tsp rosemary", "Salt and pepper to taste",
            "Optional: fresh parsley for garnish"
        )
        else -> ingredientList.map { "1 portion $it" } + listOf("Salt and pepper to taste", "Olive oil as needed")
    }

    val recipeSteps = when {
        recipeName.contains("Chicken Fried Rice") -> listOf(
            "Heat oil in a large wok or skillet over high heat",
            "Add diced chicken and cook until golden brown, about 5-6 minutes",
            "Push chicken to one side, add beaten eggs and scramble",
            "Add garlic and onion, stir-fry for 2 minutes until fragrant",
            "Add cooked rice, breaking up any clumps with a spatula",
            "Stir in soy sauce and sesame oil, toss everything together",
            "Cook for 3-4 minutes until heated through",
            "Garnish with green onions and serve immediately"
        )
        recipeName.contains("Mediterranean Pasta") -> listOf(
            "Cook pasta according to package directions until al dente",
            "While pasta cooks, heat olive oil in a large pan",
            "Add minced garlic and sauté for 1 minute until fragrant",
            "Add cherry tomatoes and cook for 3-4 minutes until they start to burst",
            "Drain pasta and add to the pan with tomatoes",
            "Toss in olives, fresh basil, and crumbled feta cheese",
            "Season with salt and pepper to taste",
            "Serve immediately with extra feta if desired"
        )
        recipeName.contains("Veggie Scramble") -> listOf(
            "Heat olive oil in a large non-stick pan over medium heat",
            "Add chopped onion and bell pepper, cook for 3-4 minutes",
            "Add spinach and cook until wilted, about 1-2 minutes",
            "Beat eggs in a bowl and season with salt and pepper",
            "Pour eggs into the pan with vegetables",
            "Gently scramble everything together for 2-3 minutes",
            "Add shredded cheese and fold in until melted",
            "Garnish with fresh herbs and serve hot"
        )
        recipeName.contains("Roasted Potato") -> listOf(
            "Preheat oven to 425°F (220°C)",
            "Wash and cube potatoes into 1-inch pieces",
            "Toss potatoes with olive oil, garlic powder, paprika, and rosemary",
            "Season generously with salt and pepper",
            "Spread on a baking sheet in a single layer",
            "Roast for 25-30 minutes until golden and crispy",
            "Flip halfway through cooking for even browning",
            "Garnish with fresh parsley and serve hot"
        )
        else -> listOf(
            "Prepare all ingredients by washing and chopping as needed",
            "Heat olive oil in a large pan over medium heat",
            "Add ingredients starting with those that take longest to cook",
            "Season with salt and pepper throughout cooking",
            "Cook until ingredients are tender and flavors are well combined",
            "Adjust seasoning to taste and serve hot"
        )
    }

    return Recipe(
        id = UUID.randomUUID().toString(),
        name = recipeName,
        ingredients = recipeIngredients,
        steps = recipeSteps,
        cookingTime = minOf(maxTime, when {
            recipeName.contains("Fried Rice") -> 20
            recipeName.contains("Pasta") -> 25
            recipeName.contains("Scramble") -> 15
            recipeName.contains("Roasted") -> 35
            else -> 30
        }),
        servings = servings,
        rating = 0f,
        dietaryTags = dietaryTags,
        imageUrl = when {
            recipeName.contains("Chicken") -> "https://images.unsplash.com/photo-1617093727343-374698b1b08d"
            recipeName.contains("Pasta") -> "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b"
            recipeName.contains("Egg") -> "https://images.unsplash.com/photo-1533089860892-a7c6f0a88666"
            recipeName.contains("Potato") -> "https://images.unsplash.com/photo-1518013431117-eb1465fa5752"
            else -> "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445"
        },
        createdAt = Date(),
        updatedAt = Date()
    )
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf("splash") }

            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    "splash" -> SplashScreen { currentScreen = "welcome" }
                    "welcome" -> WelcomeScreen { currentScreen = "main" }
                    "main" -> MainScreen()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val scale by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    LaunchedEffect(Unit) {
        delay(3000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4CAF50),
                        Color(0xFF2E7D32)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🍳",
                fontSize = 80.sp,
                modifier = Modifier.scale(scale)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Recipe Tracker",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "with AI Assistant",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1976D2),
                        Color(0xFF0D47A1)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo - Same as splash screen
            Text(
                text = "🍳",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Welcome to Recipe Tracker",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your personal cooking companion with AI-powered recipe suggestions",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    FeatureItem("🤖", "AI Recipe Assistant", "Get personalized recipes from your ingredients")
                    Spacer(modifier = Modifier.height(16.dp))
                    FeatureItem("📸", "Camera & Gallery", "Capture and upload recipe photos")
                    Spacer(modifier = Modifier.height(16.dp))
                    FeatureItem("🔍", "Advanced Search", "Find recipes by ingredients, tags, or ratings")
                    Spacer(modifier = Modifier.height(16.dp))
                    FeatureItem("📊", "Cooking History", "Track your culinary journey")
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF667eea)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FeatureItem(icon: String, title: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: RecipeViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var recipeToEdit by remember { mutableStateOf<Recipe?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilters by remember { mutableStateOf<Set<DietaryTag>>(setOf()) }
    var showAIAssistant by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(SortOption.CREATED_DATE) }

    // FIXED: Remove .collectAsState() since your ViewModel uses State, not StateFlow
    val recipes by viewModel.recipes          // ← REMOVE .collectAsState()
    val cookingHistory by viewModel.cookingHistory  // ← REMOVE .collectAsState()

    // ... rest of your MainScreen code remains exactly the same

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = { showAIAssistant = true }) {
                        Icon(Icons.Default.Psychology, contentDescription = "AI Assistant")
                    }
                    IconButton(onClick = { showSortSheet = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    recipeToEdit = null
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Recipe")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Recipes") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Cooking History") }
                )
            }

            when (selectedTabIndex) {
                0 -> RecipesTab(
                    recipes = recipes,
                    cookingHistory = cookingHistory, // ← ADD THIS LINE
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedFilters = selectedFilters,
                    onFiltersChange = { selectedFilters = it },
                    sortOption = sortOption,
                    onEditRecipe = { recipe ->
                        recipeToEdit = recipe
                        showAddDialog = true
                    },
                    onDeleteRecipe = { recipe ->
                        viewModel.deleteRecipe(recipe)
                    },
                    onCookRecipe = { recipe ->
                        viewModel.addCookingEntry(recipe)
                    }
                )

                1 -> CookingHistoryTab(
                    cookingHistory = cookingHistory,
                    recipes = recipes,
                    onDeleteEntry = viewModel::deleteCookingEntry
                )
            }
        }
    }


    // Dialogs
    AddEditRecipeDialog(
        recipe = recipeToEdit,
        isOpen = showAddDialog,
        onDismiss = {
            showAddDialog = false
            recipeToEdit = null
        },
        onSave = { recipe ->
            if (recipeToEdit == null) {
                viewModel.addRecipe(recipe)
            } else {
                viewModel.updateRecipe(recipe)
            }
            showAddDialog = false
            recipeToEdit = null
        }
    )

    // FIXED: Add missing parameters to AIRecipeAssistantDialog
    AIRecipeAssistantDialog(
        isOpen = showAIAssistant,
        onDismiss = { showAIAssistant = false },
        onRecipeGenerated = { recipe ->
            viewModel.addRecipe(recipe)
            showAIAssistant = false
        }
    )

    if (showSortSheet) {
        SortBottomSheet(
            currentSort = sortOption,
            onSortSelected = { option ->
                sortOption = option
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }
}

object SoundNotificationHelper {

    private var mediaPlayer: MediaPlayer? = null

    // Method 1: Use system notification sound
    fun playRecipeSavedSound(context: Context) {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notification)
            ringtone?.play()
            Log.d("SOUND", "Recipe saved sound played successfully")
        } catch (e: Exception) {
            Log.e("SOUND", "Error playing recipe saved sound: ${e.message}")
        }
    }

    // Method 2: Use custom success sound (more professional)
    fun playSuccessSound(context: Context) {
        try {
            // Using system success sound
            val successSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, successSound)
            mediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
            }
            mediaPlayer?.start()
            Log.d("SOUND", "Success sound played")
        } catch (e: Exception) {
            Log.e("SOUND", "Error playing success sound: ${e.message}")
        }
    }

    // Method 3: Use system "complete" sound (cooking theme)
    fun playCookingSuccessSound(context: Context) {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, uri)
            ringtone?.play()

            // Optional: Add a slight delay for a more pleasant experience
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                // Sound finishes playing
            }, 1000)

            Log.d("SOUND", "Cooking success sound played")
        } catch (e: Exception) {
            Log.e("SOUND", "Error playing cooking success sound: ${e.message}")
        }
    }
}
// FIXED: Correct order for cameraLauncher and cameraPermissionLauncher

@Composable
fun AddEditRecipeDialog(
    recipe: Recipe? = null,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSave: (Recipe) -> Unit
) {
    val context = LocalContext.current

    // Camera and permission states
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // URI management
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // FIXED: Improved createImageFile function
    fun createImageFile(): Uri? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "RECIPE_${timeStamp}.jpg"

            // Create the images directory if it doesn't exist
            val storageDir = File(context.cacheDir, "images")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            val photoFile = File(storageDir, imageFileName)

            // Use FileProvider to get secure URI
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
        } catch (e: Exception) {
            Log.e("CreateImageFile", "Error creating image file", e)
            null
        }
    }

    // FIXED: Declare cameraLauncher FIRST (before cameraPermissionLauncher)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            selectedImageUri = photoUri
            Log.d("Camera", "Photo captured successfully: $photoUri")
        } else {
            Log.e("Camera", "Failed to capture photo or photoUri is null")
        }
    }

    // FIXED: Declare cameraPermissionLauncher AFTER cameraLauncher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            // Launch camera immediately after permission granted
            photoUri = createImageFile()
            photoUri?.let { uri ->
                cameraLauncher.launch(uri)  // ← This should work now
            }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    // Recipe form states
    var name by remember(recipe, isOpen) { mutableStateOf(recipe?.name ?: "") }
    var ingredients by remember(recipe, isOpen) { mutableStateOf(recipe?.ingredients?.joinToString("\n") ?: "") }
    var steps by remember(recipe, isOpen) { mutableStateOf(recipe?.steps?.joinToString("\n") ?: "") }
    var cookingTime by remember(recipe, isOpen) { mutableStateOf(recipe?.cookingTime?.toString() ?: "") }
    var servings by remember(recipe, isOpen) { mutableStateOf(recipe?.servings?.toString() ?: "") }
    var selectedTags by remember(recipe, isOpen) { mutableStateOf<Set<DietaryTag>>(recipe?.dietaryTags?.toSet() ?: setOf()) }
    var imageUrl by remember { mutableStateOf(recipe?.imageUrl ?: "") }

    // Update imageUrl when selectedImageUri changes
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let {
            imageUrl = it.toString()
        }
    }

    if (isOpen) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (recipe == null) "Add Recipe" else "Edit Recipe",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Recipe Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Recipe Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Image Section
                    Text(
                        text = "Recipe Image",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Image Preview
                    if (selectedImageUri != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(selectedImageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Selected recipe image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // Image Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Gallery Button
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gallery")
                        }

                        // FIXED: Camera Button with proper permission handling
                        OutlinedButton(
                            onClick = {
                                if (hasCameraPermission) {
                                    photoUri = createImageFile()
                                    photoUri?.let { uri ->
                                        Log.d("Camera", "Launching camera with URI: $uri")
                                        cameraLauncher.launch(uri)  // ← This should work now
                                    } ?: run {
                                        Log.e("Camera", "Failed to create image file")
                                    }
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Camera")
                        }

                        // Clear Button
                        if (selectedImageUri != null) {
                            OutlinedButton(
                                onClick = {
                                    selectedImageUri = null
                                    imageUrl = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear")
                            }
                        }
                    }

                    // ... rest of your dialog content (Quick Select, Ingredients, etc.)

                    // Quick Select Images
                    Text("Quick Select")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            listOf(
                                "🍝" to "Italian",
                                "🍜" to "Asian",
                                "🧁" to "Dessert",
                                "🥗" to "Salad",
                                "🍳" to "Breakfast"
                            )
                        ) { (emoji, category) ->
                            Card(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clickable {
                                        imageUrl = emoji
                                        selectedImageUri = null
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (imageUrl == emoji)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = emoji,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = category,
                                        fontSize = 8.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Ingredients
                    OutlinedTextField(
                        value = ingredients,
                        onValueChange = { ingredients = it },
                        label = { Text("Ingredients (one per line)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )

                    // Steps
                    OutlinedTextField(
                        value = steps,
                        onValueChange = { steps = it },
                        label = { Text("Instructions (one step per line)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )

                    // Cooking Time and Servings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cookingTime,
                            onValueChange = { cookingTime = it },
                            label = { Text("Cooking Time (min)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = servings,
                            onValueChange = { servings = it },
                            label = { Text("Servings") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Dietary Tags
                    Text(
                        text = "Dietary Tags",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(DietaryTag.entries) { tag ->
                            FilterChip(
                                onClick = {
                                    selectedTags = if (selectedTags.contains(tag)) {
                                        selectedTags - tag
                                    } else {
                                        selectedTags + tag
                                    }
                                },
                                label = { Text(tag.displayName) },
                                selected = selectedTags.contains(tag),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tag.color,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val newRecipe = Recipe(
                                    id = recipe?.id ?: UUID.randomUUID().toString(),
                                    name = name,
                                    ingredients = ingredients.split("\n")
                                        .filter { it.isNotBlank() },
                                    steps = steps.split("\n").filter { it.isNotBlank() },
                                    cookingTime = cookingTime.toIntOrNull()
                                        ?: 0, // ← CONVERT STRING TO INT
                                    servings = servings.toIntOrNull()
                                        ?: 1, // ← CONVERT STRING TO INT
                                    rating = recipe?.rating ?: 0f,
                                    dietaryTags = selectedTags.toList(),
                                    imageUrl = selectedImageUri?.toString() ?: recipe?.imageUrl
                                    ?: "",
                                    createdAt = recipe?.createdAt ?: Date(),
                                    updatedAt = Date()
                                )

                                // Add sound notification

                                if (recipe == null) {
                                    SoundNotificationHelper.playCookingSuccessSound(context)
                                } else {
                                    SoundNotificationHelper.playSuccessSound(context)
                                }

                                onSave(newRecipe)
                            }
                        ) {
                            Text(if (recipe == null) "Add Recipe" else "Update Recipe")
                        }
                    }
                }
            }
        }
    }
}

// FIXED: Complete AIRecipeAssistantDialog with proper parameters
@Composable
fun AIRecipeAssistantDialog(
    isOpen: Boolean,  // FIXED: Add missing parameter
    onDismiss: () -> Unit,
    onRecipeGenerated: (Recipe) -> Unit  // FIXED: Add missing parameter
) {
    val context = LocalContext.current
    if (!isOpen) return  // FIXED: Only show when open

    var ingredients by remember { mutableStateOf("") }
    var maxCookingTime by remember { mutableStateOf("30") }
    var servings by remember { mutableStateOf("4") }
    var selectedDietaryTags by remember { mutableStateOf<Set<DietaryTag>>(emptySet()) }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var shouldGenerate by remember { mutableStateOf(false) }

    LaunchedEffect(shouldGenerate) {
        if (shouldGenerate) {
            delay(2000)
            generatedRecipe = generateAIRecipe(
                ingredients = ingredients,
                maxTime = maxCookingTime.toIntOrNull() ?: 30,
                servings = servings.toIntOrNull() ?: 4,
                dietaryTags = selectedDietaryTags.toList()
            )
            // 🎵 ADD THIS LINE - Play sound when AI recipe is generated
            SoundNotificationHelper.playCookingSuccessSound(context)

            isGenerating = false
            shouldGenerate = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Recipe Assistant",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AI Tips Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Tips for Better Results",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• List specific ingredients (e.g., 'chicken breast, rice, bell peppers')\n" +
                                    "• Include quantities if important (e.g., '2 eggs, 1 cup flour')\n" +
                                    "• Mention cooking style preferences (e.g., 'quick stir-fry')\n" +
                                    "• Set realistic time limits for your schedule",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ingredients Input
                OutlinedTextField(
                    value = ingredients,
                    onValueChange = { ingredients = it },
                    label = { Text("Available Ingredients") },
                    placeholder = { Text("e.g., chicken, rice, onions, bell peppers...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    minLines = 4,
                    leadingIcon = {
                        Icon(Icons.Default.Kitchen, contentDescription = null)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Preferences
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = maxCookingTime,
                        onValueChange = { maxCookingTime = it },
                        label = { Text("Max Time (min)") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(Icons.Default.Timer, contentDescription = null)
                        }
                    )
                    OutlinedTextField(
                        value = servings,
                        onValueChange = { servings = it },
                        label = { Text("Servings") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(Icons.Default.People, contentDescription = null)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dietary Preferences
                Text(
                    text = "Dietary Preferences (Optional)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DietaryTag.entries.toList()) { tag ->
                        FilterChip(
                            onClick = {
                                selectedDietaryTags = if (selectedDietaryTags.contains(tag)) {
                                    selectedDietaryTags - tag
                                } else {
                                    selectedDietaryTags + tag
                                }
                            },
                            label = { Text(tag.displayName) },
                            selected = selectedDietaryTags.contains(tag),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = tag.color,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Generate Button
                Button(
                    onClick = {
                        if (ingredients.isNotBlank()) {
                            isGenerating = true
                            shouldGenerate = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = ingredients.isNotBlank() && !isGenerating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("AI is cooking up something amazing...")
                    } else {
                        Icon(Icons.Default.Psychology, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generate Recipe with AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Generated Recipe Dialog
    generatedRecipe?.let { recipe ->
        GeneratedRecipeDialog(
            recipe = recipe,
            onDismiss = { generatedRecipe = null },
            onSaveRecipe = {
                onRecipeGenerated(recipe)  // FIXED: Use the callback
                generatedRecipe = null
            }
        )
    }
}
@Composable
fun GeneratedRecipeDialog(
    recipe: Recipe,
    onDismiss: () -> Unit,
    onSaveRecipe: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Generated Recipe",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recipe Image
                if (recipe.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(recipe.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Recipe Name
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Recipe Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoChip(
                        icon = Icons.Default.Timer,
                        text = "${recipe.cookingTime} min"
                    )
                    InfoChip(
                        icon = Icons.Default.People,
                        text = "${recipe.servings} servings"
                    )
                    InfoChip(
                        icon = Icons.Default.Psychology,
                        text = "AI Generated"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dietary Tags
                if (recipe.dietaryTags.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recipe.dietaryTags) { tag ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        tag.color,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Ingredients
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                recipe.ingredients.forEach { ingredient ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = ingredient,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Instructions
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                recipe.steps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Generate Another")
                    }

                    Button(
                        onClick = onSaveRecipe,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Recipe")
                    }
                }
            }
        }
    }
}

// Additional composable functions needed for the complete app
// FIXED: Complete RecipesTab function
@Composable
fun RecipesTab(
    recipes: List<Recipe>,
    cookingHistory: List<CookingEntry>, // ← ADD THIS PARAMETER
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilters: Set<DietaryTag>,
    onFiltersChange: (Set<DietaryTag>) -> Unit,
    sortOption: SortOption,
    onEditRecipe: (Recipe) -> Unit,
    onDeleteRecipe: (Recipe) -> Unit,
    onCookRecipe: (Recipe) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search recipes...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Filter Chips
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DietaryTag.entries.toList()) { tag ->
                FilterChip(
                    onClick = {
                        onFiltersChange(
                            if (selectedFilters.contains(tag)) {
                                selectedFilters - tag
                            } else {
                                selectedFilters + tag
                            }
                        )
                    },
                    label = { Text(tag.displayName) },
                    selected = selectedFilters.contains(tag),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = tag.color,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FIXED: Filtering and sorting logic
        val filteredRecipes = remember(recipes, searchQuery, selectedFilters, sortOption) {
            recipes.filter { recipe ->
                val matchesSearch = recipe.name.contains(searchQuery, ignoreCase = true) ||
                        recipe.ingredients.any { it.contains(searchQuery, ignoreCase = true) }
                val matchesFilters = selectedFilters.isEmpty() ||
                        selectedFilters.any { recipe.dietaryTags.contains(it) }
                matchesSearch && matchesFilters
            }.let { filtered ->
                when (sortOption) {
                    SortOption.NAME -> filtered.sortedBy { it.name }
                    SortOption.COOKING_TIME -> filtered.sortedBy { it.cookingTime }
                    SortOption.RATING -> filtered.sortedByDescending { it.rating }
                    SortOption.CREATED_DATE -> filtered.sortedByDescending { it.createdAt }  // ← FIXED
                    SortOption.LAST_COOKED -> filtered.sortedByDescending { it.updatedAt }
                }
            }
        }

        // Recipe List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredRecipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    cookingHistory = cookingHistory, // ← NOW THIS WILL WORK
                    onEdit = { onEditRecipe(recipe) },
                    onDelete = { onDeleteRecipe(recipe) },
                    onCook = { onCookRecipe(recipe) }
                )
            }
        }
    }
}

// FIXED: Complete RecipeCard function
// Updated RecipeCard with Delete functionality

@Composable
fun RecipeCard(
    recipe: Recipe,
    cookingHistory: List<CookingEntry>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCook: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { showDetails = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Recipe Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (recipe.rating > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = recipe.rating.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recipe Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (recipe.cookingTime > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.cookingTime}m",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (recipe.servings > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.servings} servings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Dietary Tags
            if (recipe.dietaryTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(recipe.dietaryTags) { tag ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = tag.displayName,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = tag.color.copy(alpha = 0.2f),
                                labelColor = tag.color
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons - UPDATED with Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // View Button
                OutlinedButton(
                    onClick = { showDetails = true },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View", style = MaterialTheme.typography.labelMedium)
                }

                // Edit Button
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", style = MaterialTheme.typography.labelMedium)
                }

                // Delete Button - NEW
                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", style = MaterialTheme.typography.labelMedium)
                }

                // Cook Button
                Button(
                    onClick = onCook,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cook", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }

    // Recipe Details Dialog
    if (showDetails) {
        RecipeDetailsDialog(
            recipe = recipe,
            cookingHistory = cookingHistory.filter { it.recipeId == recipe.id },
            onDismiss = { showDetails = false }
        )
    }

    // Delete Confirmation Dialog - NEW
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = "Delete Recipe",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${recipe.name}\"? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
fun RatingDisplay(rating: Float) {
    Row {
        repeat(5) { index ->
            Icon(
                if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (index < rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CookingHistoryTab(
    cookingHistory: List<CookingEntry>,
    recipes: List<Recipe>,
    onDeleteEntry: (CookingEntry) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cookingHistory.sortedByDescending { it.cookedAt }) { entry ->
            val recipe = recipes.find { it.id == entry.recipeId }
            if (recipe != null) {
                CookingHistoryCard(
                    entry = entry,
                    recipe = recipe,
                    onDelete = { onDeleteEntry(entry) }
                )
            }
        }
    }
}

@Composable
fun CookingHistoryCard(
    entry: CookingEntry,
    recipe: Recipe,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(entry.cookedAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (entry.personalRating > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingDisplay(rating = entry.personalRating)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun RecipeDetailsDialog(
    recipe: Recipe,
    cookingHistory: List<CookingEntry>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recipe Image
                if (recipe.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(recipe.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Recipe Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoChip(
                        icon = Icons.Default.Timer,
                        text = "${recipe.cookingTime} min"
                    )
                    InfoChip(
                        icon = Icons.Default.People,
                        text = "${recipe.servings} servings"
                    )
                    InfoChip(
                        icon = Icons.Default.History,
                        text = "${cookingHistory.size} times cooked"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dietary Tags
                if (recipe.dietaryTags.isNotEmpty()) {
                    Text(
                        text = "Dietary Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recipe.dietaryTags) { tag ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        tag.color,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Ingredients
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                recipe.ingredients.forEach { ingredient ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = ingredient,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Instructions
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                recipe.steps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    recipeName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Recipe") },
        text = { Text("Are you sure you want to delete \"$recipeName\"? This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(true) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                onDismiss()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Sort Recipes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SortOption.entries.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSortSelected(option)
                                showBottomSheet = false
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSort == option,
                            onClick = {
                                onSortSelected(option)
                                showBottomSheet = false
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = when (option) {
                                SortOption.NAME -> "Name (A-Z)"
                                SortOption.RATING -> "Rating (High to Low)"
                                SortOption.COOKING_TIME -> "Cooking Time (Low to High)"
                                SortOption.CREATED_DATE -> "Recently Added"
                                SortOption.LAST_COOKED -> "Recently Cooked"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
