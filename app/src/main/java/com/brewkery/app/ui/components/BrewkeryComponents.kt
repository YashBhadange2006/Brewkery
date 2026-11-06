package com.brewkery.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.brewkery.app.data.model.MenuItem
import com.brewkery.app.data.model.formatPrice
import com.brewkery.app.ui.theme.BorderWarm
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.Latte
import com.brewkery.app.ui.theme.Mocha
import com.brewkery.app.ui.theme.SurfaceWhite
import com.brewkery.app.ui.theme.Terracotta

@Composable
fun CartButton(count: Int, onClick: () -> Unit) {
    Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.size(44.dp).semantics {
                contentDescription = if (count == 1) "Cart, 1 item" else "Cart, $count items"
            }.clickable(onClick = onClick),
            shape = CircleShape,
            color = SurfaceWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
        ) {
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                CartIcon(Modifier.size(22.dp), Espresso)
            }
        }
        if (count > 0) {
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).size(17.dp).offset(x = 4.dp, y = (-4).dp),
                color = Terracotta,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(if (count > 9) "9+" else count.toString(), color = SurfaceWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CartIcon(modifier: Modifier = Modifier, color: androidx.compose.ui.graphics.Color) {
    Canvas(modifier) {
        val unit = size.minDimension / 24f
        val strokeWidth = 1.8f * unit
        fun point(x: Float, y: Float) = Offset(x * unit, y * unit)
        drawLine(color, point(2f, 3f), point(5f, 3f), strokeWidth, cap = StrokeCap.Round)
        drawLine(color, point(5f, 3f), point(8f, 14f), strokeWidth, cap = StrokeCap.Round)
        val basket = Path().apply {
            moveTo(7f * unit, 6f * unit)
            lineTo(22f * unit, 6f * unit)
            lineTo(19.5f * unit, 13f * unit)
            lineTo(8f * unit, 13f * unit)
            close()
        }
        drawPath(basket, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        drawCircle(color, radius = 1.5f * unit, center = point(10f, 19f))
        drawCircle(color, radius = 1.5f * unit, center = point(18f, 19f))
    }
}

@Composable
fun PageHeader(title: String, modifier: Modifier = Modifier, onBack: (() -> Unit)? = null, action: (@Composable () -> Unit)? = null) {
    Row(modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) {
            Surface(shape = CircleShape, color = SurfaceWhite, tonalElevation = 1.dp, modifier = Modifier.size(42.dp).clickable(onClick = onBack)) {
                Box(contentAlignment = Alignment.Center) { Text("‹", fontSize = 28.sp, color = Espresso) }
            }
        } else Spacer(Modifier.width(4.dp))
        Text(title, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 14.sp, color = Espresso)
        Box(Modifier.width(46.dp), contentAlignment = Alignment.CenterEnd) { action?.invoke() }
    }
}

@Composable
fun SectionHeading(title: String, modifier: Modifier = Modifier) {
    Text(title, modifier = modifier.padding(bottom = 10.dp), color = Espresso, fontWeight = FontWeight.Bold, fontSize = 15.sp)
}

@Composable
fun SearchBox(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = modifier.fillMaxWidth(), singleLine = true,
        placeholder = { Text("Search roast, cold brew, pastry…", color = Mocha) },
        leadingIcon = { Text("⌕", fontSize = 22.sp, color = Mocha) },
        trailingIcon = if (value.isNotEmpty()) ({ Text("×", Modifier.clickable { onValueChange("") }.padding(8.dp), color = Mocha) }) else null,
        shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Terracotta, unfocusedBorderColor = BorderWarm,
            focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite
        ))
}

@Composable
fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(modifier = Modifier.clickable(onClick = onClick), shape = RoundedCornerShape(50),
        color = if (selected) Espresso else SurfaceWhite,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)) {
        Text(label, modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp), color = if (selected) Cream else Mocha,
            fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
fun MenuProductCard(item: MenuItem, currencySymbol: String, onOpen: () -> Unit) {
    Surface(shape = RoundedCornerShape(18.dp), color = SurfaceWhite, border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
        tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen)) {
        Row(Modifier.padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = item.imageUrl, contentDescription = item.name, contentScale = ContentScale.Crop,
                modifier = Modifier.size(76.dp).clip(RoundedCornerShape(13.dp)).background(Latte))
            Column(Modifier.weight(1f).padding(start = 11.dp, end = 8.dp)) {
                if (item.badge.isNotBlank()) Surface(color = androidx.compose.ui.graphics.Color(0xFFFFF0C3), shape = RoundedCornerShape(6.dp)) {
                    Text(item.badge, Modifier.padding(horizontal = 6.dp, vertical = 3.dp), color = Mocha, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Text(item.name, Modifier.padding(top = 3.dp), color = Espresso, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
                Text("★  ${item.rating} (${item.reviewCount})", Modifier.padding(top = 3.dp), color = Mocha, fontSize = 11.sp)
                Text(formatPrice(item.basePrice, currencySymbol), Modifier.padding(top = 3.dp), color = Terracotta, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Button(onClick = onOpen, shape = RoundedCornerShape(9.dp), contentPadding = ButtonDefaults.ContentPadding,
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = SurfaceWhite)) {
                Text("+ Customize", fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}

@Composable
fun PrimaryAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(onClick = onClick, modifier = modifier.height(54.dp), enabled = enabled, shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Terracotta, disabledContainerColor = BorderWarm)) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun LoadingOrError(message: String, loading: Boolean, onRetry: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        if (loading) CircularProgressIndicator(color = Terracotta) else {
            Text("☕", fontSize = 38.sp)
            Spacer(Modifier.height(10.dp))
            Text(message, color = Mocha, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            if (onRetry != null) { Spacer(Modifier.height(14.dp)); OutlinedButton(onClick = onRetry) { Text("Try again", color = Terracotta) } }
        }
    }
}

@Composable
fun QuantityControl(quantity: Int, onChange: (Int) -> Unit, compact: Boolean = false) {
    val size = if (compact) 32.dp else 38.dp
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        QuantityStep("−", size, enabled = quantity > 0) { onChange(-1) }
        Text(quantity.toString(), color = Espresso, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        QuantityStep("+", size) { onChange(1) }
    }
}

@Composable
private fun QuantityStep(label: String, size: androidx.compose.ui.unit.Dp, enabled: Boolean = true, onClick: () -> Unit) {
    Surface(modifier = Modifier.size(size).clickable(enabled = enabled, onClick = onClick), color = Cream, shape = CircleShape) {
        Box(contentAlignment = Alignment.Center) { Text(label, fontWeight = FontWeight.Bold, color = if (enabled) Espresso else Mocha) }
    }
}

@Composable
fun OptionButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(11.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) Terracotta else BorderWarm),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) Latte else SurfaceWhite, contentColor = if (selected) Terracotta else Mocha),
        contentPadding = ButtonDefaults.ContentPadding) {
        Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
