package com.abraham.personalfinancemanagementapp.presentation.screens.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abraham.personalfinancemanagementapp.ui.theme.PrimaryBlue
import com.abraham.personalfinancemanagementapp.ui.theme.PrimaryPurple
import kotlinx.coroutines.delay

// Onboarding page data
data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradientColors: List<Color>
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = "Track Your Expenses",
        description = "Monitor all your spending in one place",
        icon = Icons.Default.AccountBalanceWallet,
        gradientColors = listOf(
            PrimaryPurple,
            PrimaryBlue
        )
    ),
    OnboardingPage(
        title = "Manage Your Budget",
        description = "Set budgets and stay on track",
        icon = Icons.Default.AccountBalanceWallet,
        gradientColors = listOf(
            PrimaryBlue,
            Color(0xFF00F2FE)
        )
    ),
    OnboardingPage(
        title = "Analyze Your Spending",
        description = "Get insights into your financial habits",
        icon = Icons.Default.AccountBalanceWallet,
        gradientColors = listOf(
            Color(0xFF00F2FE),
            Color(0xFF43E97B)
        )
    ),
    OnboardingPage(
        title = "Achieve Your Goals",
        description = "Save money and reach your financial targets",
        icon = Icons.Default.AccountBalanceWallet,
        gradientColors = listOf(
            Color(0xFFFA709A),
            Color(0xFFFEE140)
        )
    )
)

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPageIndex by remember { mutableIntStateOf(0) }
    var previousPageIndex by remember { mutableIntStateOf(0) }
    var isTransitioning by remember { mutableStateOf(false) }

    val currentPage = onboardingPages[currentPageIndex]

    // Scale animation for the background
    val scaleTransition = rememberInfiniteTransition(label = "scale")
    val scale by scaleTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Fade animation for transitions
    var targetAlpha by remember { mutableStateOf(1f) }
    val fadeAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(600, easing = LinearEasing),
        label = "fade"
    )

    // Auto-transition every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            if (!isTransitioning && currentPageIndex < onboardingPages.size - 1) {
                isTransitioning = true
                previousPageIndex = currentPageIndex
                
                // Fade out
                targetAlpha = 0f
                delay(600)
                
                // Change page
                currentPageIndex++
                
                // Fade in
                targetAlpha = 1f
                delay(600)
                
                isTransitioning = false
            } else if (currentPageIndex >= onboardingPages.size - 1) {
                // Reset to first page
                isTransitioning = true
                previousPageIndex = currentPageIndex
                
                targetAlpha = 0f
                delay(600)
                
                currentPageIndex = 0
                
                targetAlpha = 1f
                delay(600)
                
                isTransitioning = false
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background gradient with animation
        Box(modifier = Modifier.fillMaxSize()) {
            // Previous page (fading out)
            if (isTransitioning && previousPageIndex != currentPageIndex) {
                val previousPage = onboardingPages[previousPageIndex]
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = previousPage.gradientColors
                            )
                        )
                        .scale(scale)
                        .alpha(1f - fadeAlpha)
                )
            }
            
            // Current page (scaling and fading in)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = currentPage.gradientColors
                        )
                    )
                    .scale(scale)
                    .alpha(fadeAlpha)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "Skip",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Icon
            Icon(
                imageVector = currentPage.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp),
                tint = Color(0xFFFFD700) // Gold/Yellow color for money icon
            )

            // Title
            Text(
                text = currentPage.title,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Description
            Text(
                text = currentPage.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Pagination dots
            Row(
                modifier = Modifier.padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                onboardingPages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPageIndex) 10.dp else 8.dp)
                            .background(
                                color = if (index == currentPageIndex) 
                                    Color.White 
                                else 
                                    Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }

            // Get Started Button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
