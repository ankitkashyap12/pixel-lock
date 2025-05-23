//package com.pixel.applock.ui
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.graphics.painter.BitmapPainter
//import androidx.compose.ui.unit.dp
//import androidx.core.graphics.drawable.toBitmap
//import com.pixel.applock.data.AppInfo
//
//@Composable
//fun AppSelectionScreen(apps: List<AppInfo>, onAppSelected: (AppInfo) -> Unit) {
//    LazyColumn {
//        items(apps) { app ->
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { onAppSelected(app) }
//                    .padding(16.dp)
//            ) {
//                val bitmap = remember(app.icon) { app.icon.toBitmap() }
//                val painter = remember(bitmap) { BitmapPainter(bitmap.asImageBitmap()) }
//                Icon(painter = painter, contentDescription = null)
//                Spacer(modifier = Modifier.width(16.dp))
//                Text(text = app.name)
//            }
//        }
//    }
//}