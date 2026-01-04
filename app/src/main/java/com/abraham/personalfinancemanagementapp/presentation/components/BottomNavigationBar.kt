package com.abraham.personalfinancemanagementapp.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.abraham.personalfinancemanagementapp.presentation.navigation.Screen
import com.abraham.personalfinancemanagementapp.ui.theme.PrimaryBlue
import com.abraham.personalfinancemanagementapp.ui.theme.PrimaryPurple

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    data object Home : BottomNavItem(Screen.Home.route, Icons.Default.Home, "Home")
    data object Reports : BottomNavItem(Screen.Reports.route, Icons.Default.BarChart, "Reports")
    data object Budgets : BottomNavItem(Screen.BudgetList.route, Icons.Default.TrackChanges, "Budgets")
    data object Settings : BottomNavItem(Screen.Settings.route, Icons.Default.Settings, "Settings")
}

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            BottomNavItem(
                item = BottomNavItem.Home,
                isSelected = currentRoute == BottomNavItem.Home.route,
                onClick = { onNavigate(BottomNavItem.Home.route) }
            )
            
            // Reports
            BottomNavItem(
                item = BottomNavItem.Reports,
                isSelected = currentRoute == BottomNavItem.Reports.route,
                onClick = { onNavigate(BottomNavItem.Reports.route) }
            )
            
            // Add button in the center
            AddFloatingButton(onClick = onAddClick)
            
            // Budgets
            BottomNavItem(
                item = BottomNavItem.Budgets,
                isSelected = currentRoute == BottomNavItem.Budgets.route,
                onClick = { onNavigate(BottomNavItem.Budgets.route) }
            )
            
            // Settings
            BottomNavItem(
                item = BottomNavItem.Settings,
                isSelected = currentRoute == BottomNavItem.Settings.route,
                onClick = { onNavigate(BottomNavItem.Settings.route) }
            )
        }
    }
}

@Composable
private fun AddFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300),
        label = "fab_scale"
    )
    
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .offset(y = (-12).dp)
            .scale(scale),
        containerColor = Color.Transparent,
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryPurple, PrimaryBlue)
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(300),
        label = "nav_item_scale"
    )
    
    val iconColor by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.6f,
        animationSpec = tween(300),
        label = "icon_color"
    )
    
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(scale)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isSelected) PrimaryBlue.copy(alpha = 0.1f)
                    else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isSelected) PrimaryBlue 
                       else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = iconColor),
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) PrimaryBlue 
                   else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = iconColor),
            maxLines = 1
        )
    }
}
