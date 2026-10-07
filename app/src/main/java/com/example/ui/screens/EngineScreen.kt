package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.config.AppLanguage
import com.example.config.ModelConfig
import com.example.config.Strings
import com.example.data.model.GeminiKeyEntity
import com.example.data.model.QueueTaskItem
import com.example.ui.components.formatTimestamp

@Composable
fun EngineScreen(
    keys: List<GeminiKeyEntity>,
    cacheCount: Int,
    activeQueue: List<QueueTaskItem>,
    dataSaverMode: Boolean,
    testingKeyId: Long?,
    testResults: Map<Long, String>,
    language: AppLanguage,
    onAddKey: (nickname: String, apiKey: String) -> Unit,
    onDeleteKey: (Long) -> Unit,
    onToggleKey: (id: Long, enabled: Boolean) -> Unit,
    onTestKey: (GeminiKeyEntity) -> Unit,
    onExportKeys: (passphrase: String) -> String,
    onImportKeys: (encryptedPackage: String, passphrase: String, onResult: (Result<Int>) -> Unit) -> Unit,
    onClearCache: () -> Unit,
    onToggleDataSaver: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    val healthyCount = keys.count { it.isEnabled && !it.isCurrentlyInCooldown() && it.status != "INVALID" }
    val totalCalls = keys.sumOf { it.callsToday }
    val totalTokens = keys.sumOf { it.tokensToday }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("engine_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // --- Engine Header Overview ---
        item {
            EngineOverviewHeader(
                healthyCount = healthyCount,
                totalKeys = keys.size,
                totalCalls = totalCalls,
                totalTokens = totalTokens,
                cacheCount = cacheCount,
                language = language,
                onAddKeyClick = { showAddDialog = true },
                onExportClick = { showExportDialog = true },
                onImportClick = { showImportDialog = true }
            )
        }

        // --- Keys List Section Header ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configured API Keys (${keys.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "+ Add New",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { showAddDialog = true }
                        .testTag("add_key_header_button")
                )
            }
        }

        if (keys.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("empty_keys_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = Strings.get("no_keys_warning", language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.testTag("add_first_key_button")
                        ) {
                            Text("Add Gemini API Key")
                        }
                    }
                }
            }
        } else {
            items(keys, key = { it.id }) { key ->
                GeminiKeyCard(
                    key = key,
                    isTesting = testingKeyId == key.id,
                    testResult = testResults[key.id],
                    onToggleEnabled = { onToggleKey(key.id, it) },
                    onTestKey = { onTestKey(key) },
                    onDelete = { onDeleteKey(key.id) },
                    language = language
                )
            }
        }

        // --- Active Request Queue & Concurrency ---
        item {
            Spacer(modifier = Modifier.height(16.dp))
            RequestQueueCard(activeQueue = activeQueue, language = language)
        }

        // --- Cache & Data Saver Section ---
        item {
            Spacer(modifier = Modifier.height(12.dp))
            CacheAndDataSaverSection(
                cacheCount = cacheCount,
                dataSaverMode = dataSaverMode,
                onClearCache = onClearCache,
                onToggleDataSaver = onToggleDataSaver,
                language = language
            )
        }

        // --- Model Profiles ---
        item {
            Spacer(modifier = Modifier.height(12.dp))
            ModelProfilesCard(language = language)
        }

        // --- Privacy & Local Storage Note ---
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SecurityNoticeCard(language = language)
        }
    }

    // Add Key Dialog
    if (showAddDialog) {
        AddKeyDialog(
            language = language,
            onDismiss = { showAddDialog = false },
            onAdd = { nickname, apiKey ->
                onAddKey(nickname, apiKey)
                showAddDialog = false
            }
        )
    }

    // Export Keys Dialog
    if (showExportDialog) {
        ExportKeysDialog(
            language = language,
            onDismiss = { showExportDialog = false },
            onGenerateExport = { passphrase -> onExportKeys(passphrase) }
        )
    }

    // Import Keys Dialog
    if (showImportDialog) {
        ImportKeysDialog(
            language = language,
            onDismiss = { showImportDialog = false },
            onImport = onImportKeys
        )
    }
}

@Composable
private fun EngineOverviewHeader(
    healthyCount: Int,
    totalKeys: Int,
    totalCalls: Int,
    totalTokens: Long,
    cacheCount: Int,
    language: AppLanguage,
    onAddKeyClick: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("engine_overview_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.get("multi_key_engine", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (healthyCount > 0) "$healthyCount of $totalKeys Keys Active" else "No active keys",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (healthyCount > 0) Color(0xFF16A34A) else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = Strings.get("engine_desc", language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))
            // Quick Stat Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatPill(label = "Calls Today", value = "$totalCalls")
                StatPill(label = "Tokens", value = "$totalTokens")
                StatPill(label = "0-Cost Hits", value = "$cacheCount")
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddKeyClick,
                    modifier = Modifier.weight(1.2f).testTag("engine_add_key_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Key", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onExportClick,
                    modifier = Modifier.weight(1f).testTag("engine_export_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Export", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onImportClick,
                    modifier = Modifier.weight(1f).testTag("engine_import_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Import", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun GeminiKeyCard(
    key: GeminiKeyEntity,
    isTesting: Boolean,
    testResult: String?,
    onToggleEnabled: (Boolean) -> Unit,
    onTestKey: () -> Unit,
    onDelete: () -> Unit,
    language: AppLanguage
) {
    val inCooldown = key.isCurrentlyInCooldown()
    val cooldownRemaining = key.remainingCooldownSeconds()

    val (statusLabel, statusColor) = when {
        !key.isEnabled -> Pair("Disabled", Color.Gray)
        inCooldown -> Pair("Cooldown (${cooldownRemaining}s)", Color(0xFFD97706))
        key.status == "RATE_LIMITED" -> Pair("Rate Limited (429)", Color(0xFFDC2626))
        key.status == "INVALID" -> Pair("Invalid Key (400)", Color(0xFFDC2626))
        key.status == "HEALTHY" -> Pair("Healthy", Color(0xFF16A34A))
        else -> Pair("Untested", Color(0xFF6B7280))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("key_card_${key.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Nickname & Enable Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = key.nickname,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Switch(
                    checked = key.isEnabled,
                    onCheckedChange = onToggleEnabled,
                    modifier = Modifier.testTag("toggle_key_${key.id}")
                )
            }

            // Masked Key Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = key.getMaskedKey(),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Calls: ${key.callsToday} • Tokens: ${key.tokensToday} • Errors: ${key.errorsCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (key.lastSuccessTime > 0) {
                    Text(
                        text = formatTimestamp(key.lastSuccessTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            // Test Feedback Banner if any
            if (testResult != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (testResult.contains("Healthy", ignoreCase = true)) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = testResult,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (testResult.contains("Healthy", ignoreCase = true)) Color(0xFF15803D) else Color(0xFFB91C1C),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Test key & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onTestKey,
                    enabled = !isTesting,
                    modifier = Modifier.testTag("test_key_btn_${key.id}"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Testing...", fontSize = 12.sp)
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Key", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp).testTag("delete_key_btn_${key.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun RequestQueueCard(
    activeQueue: List<QueueTaskItem>,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("request_queue_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HourglassTop, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("request_queue", language),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeQueue.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = if (activeQueue.isEmpty()) "Idle" else "${activeQueue.size} in queue",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            if (activeQueue.isEmpty()) {
                Text(
                    text = "No pending AI tasks. User actions are prioritized with instant dispatch.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                activeQueue.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = task.taskType, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (task.priority == 1) Color(0xFFFDE68A) else Color(0xFFE2E8F0)
                        ) {
                            Text(
                                text = if (task.priority == 1) "HIGH PRIORITY" else "BACKGROUND",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CacheAndDataSaverSection(
    cacheCount: Int,
    dataSaverMode: Boolean,
    onClearCache: () -> Unit,
    onToggleDataSaver: (Boolean) -> Unit,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("cache_datasaver_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Data Saver Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Strings.get("data_saver_mode", language),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = Strings.get("data_saver_desc", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = dataSaverMode,
                    onCheckedChange = onToggleDataSaver,
                    modifier = Modifier.testTag("data_saver_switch")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
            Spacer(modifier = Modifier.height(14.dp))

            // Cache Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${Strings.get("cached_responses", language)}: $cacheCount",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Deterministic SHA-256 hash lookup (0 API cost)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = onClearCache,
                    modifier = Modifier.testTag("clear_cache_btn")
                ) {
                    Text(Strings.get("clear_cache", language), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun ModelProfilesCard(language: AppLanguage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("model_profiles_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Model Profiles Configuration",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Fast Profile
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⚡ Fast Profile: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Text(text = ModelConfig.DEFAULT_FAST_PROFILE.modelId, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        text = if (language == AppLanguage.HINDI) ModelConfig.DEFAULT_FAST_PROFILE.descriptionHi else ModelConfig.DEFAULT_FAST_PROFILE.descriptionEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Smart Profile
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🧠 Smart Profile: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Text(text = ModelConfig.DEFAULT_SMART_PROFILE.modelId, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        text = if (language == AppLanguage.HINDI) ModelConfig.DEFAULT_SMART_PROFILE.descriptionHi else ModelConfig.DEFAULT_SMART_PROFILE.descriptionEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityNoticeCard(language: AppLanguage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("security_notice_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = Strings.get("keys_safe_note", language),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF1E40AF),
                lineHeight = 18.sp
            )
        }
    }
}

// Dialogs

@Composable
private fun AddKeyDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onAdd: (nickname: String, apiKey: String) -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.get("add_key", language), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text(Strings.get("key_nickname", language)) },
                    placeholder = { Text("e.g. My Personal Studio Key") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_key_nickname_input")
                )
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text(Strings.get("key_value", language)) },
                    placeholder = { Text("AIzaSy...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_key_value_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (apiKey.isNotBlank()) {
                        onAdd(nickname, apiKey)
                    }
                },
                enabled = apiKey.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_key_btn")
            ) {
                Text(Strings.get("save", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.get("cancel", language))
            }
        }
    )
}

@Composable
private fun ExportKeysDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onGenerateExport: (passphrase: String) -> String
) {
    val context = LocalContext.current
    var passphrase by remember { mutableStateOf("") }
    var encryptedPayload by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.get("export_keys", language), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Encrypt your keys with a master passphrase before copying or exporting.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = passphrase,
                    onValueChange = { passphrase = it },
                    label = { Text(Strings.get("passphrase", language)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("export_passphrase_input")
                )

                if (encryptedPayload != null) {
                    Text(text = "Encrypted Package:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    SelectionContainer {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.05f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = encryptedPayload!!,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(8.dp),
                                maxLines = 4
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (encryptedPayload == null) {
                Button(
                    onClick = {
                        if (passphrase.isNotBlank()) {
                            encryptedPayload = onGenerateExport(passphrase)
                        }
                    },
                    enabled = passphrase.isNotBlank(),
                    modifier = Modifier.testTag("generate_export_btn")
                ) {
                    Text("Encrypt")
                }
            } else {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("ExamPilot Encrypted Keys", encryptedPayload)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Encrypted keys copied to clipboard!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier.testTag("copy_export_btn")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Package")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.get("cancel", language))
            }
        }
    )
}

@Composable
private fun ImportKeysDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onImport: (encryptedPackage: String, passphrase: String, onResult: (Result<Int>) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var packageText by remember { mutableStateOf("") }
    var passphrase by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.get("import_keys", language), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Paste the encrypted package and enter the passphrase used during export.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = packageText,
                    onValueChange = { packageText = it },
                    label = { Text("Encrypted Package") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("import_package_input")
                )
                OutlinedTextField(
                    value = passphrase,
                    onValueChange = { passphrase = it },
                    label = { Text(Strings.get("passphrase", language)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("import_passphrase_input")
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (packageText.isNotBlank() && passphrase.isNotBlank()) {
                        onImport(packageText, passphrase) { result ->
                            result.fold(
                                onSuccess = { count ->
                                    Toast.makeText(context, "Successfully imported $count keys!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                },
                                onFailure = { error ->
                                    errorMessage = "Decryption failed. Incorrect passphrase or corrupt package."
                                }
                            )
                        }
                    }
                },
                enabled = packageText.isNotBlank() && passphrase.isNotBlank(),
                modifier = Modifier.testTag("confirm_import_btn")
            ) {
                Text(Strings.get("import_keys", language))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.get("cancel", language))
            }
        }
    )
}
