# Guía de Resaltado de Sintaxis (Syntax Highlighting) y Colores de DevVault

Esta guía documenta la implementación del resaltador de sintaxis y los colores distintivos de **DevVault** para que cualquier integrante del equipo pueda aplicarlo en sus pantallas (ej. `HomeScreen`, `FavoritosScreen`, `CategoriaDetailScreen`, `SnippetDetailScreen`).

---

## 1. Archivo Creado y Ubicación

- **Ruta del archivo:**  
  `app/src/main/java/com/devvault/app/ui/add_snippet/CodeSyntaxVisualTransformation.kt`
- **Paquete:**  
  `com.devvault.app.ui.add_snippet`
- **Clase principal:**  
  `CodeSyntaxVisualTransformation(language: String = "") : VisualTransformation`

*(Si el equipo lo prefiere en un paquete global compartido, se puede mover libremente a `com.devvault.app.ui.components` sin romper la lógica).*

---

## 2. Paleta de Colores Utilizada

Los colores respetan estrictamente la identidad visual centralizada en `com.devvault.app.ui.theme.Color.kt`:

| Elemento de Código | Token / Color | Valor Hex | Propósito Visual |
| :--- | :--- | :--- | :--- |
| **Palabras Clave / Keywords** | `PrimaryCyan` | `#33C8F2` | `fun`, `val`, `var`, `class`, `docker`, `run`, `echo`, `sudo`, etc. |
| **Comandos SQL** | `sqlKeywordColor` | `#FF79C6` | `SELECT`, `FROM`, `WHERE`, `JOIN`, `INSERT`, etc. (estilo Dracula/IDE) |
| **Cadenas de Texto / Strings** | `CodeText` | `#7FD9A8` | Textos entre comillas dobles `"..."` y simples `'...'` |
| **Números** | `StarFavorite` | `#F5C542` | Números enteros y flotantes (0-9) |
| **Comentarios** | `commentColor` | `#6B7280` | `//...` (Kotlin/Go), `#...` (Bash/Python/Docker), `--...` (SQL) |
| **Fondo de Bloques de Código**| `CodeBackground` | `#05070B` | Fondo oscuro profundo para terminal / editor de código |
| **Bordes de Contenedor** | `OutlineDark` | `#2C3552` | Líneas divisorias y bordes de tarjetas |

---

## 3. ¿Cómo Utilizarlo en Jetpack Compose?

### Caso A: En un campo de entrada o edición (`TextField` / `OutlinedTextField`)
Al utilizar `VisualTransformation`, el coloreado se aplica en tiempo real sin modificar el texto plano ni corromper saltos de línea ni tabulaciones:

```kotlin
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.devvault.app.ui.add_snippet.CodeSyntaxVisualTransformation
import com.devvault.app.ui.theme.CodeBackground
import com.devvault.app.ui.theme.CodeText
import com.devvault.app.ui.theme.OutlineDark
import com.devvault.app.ui.theme.PrimaryCyan

OutlinedTextField(
    value = codigoSnippet,
    onValueChange = { codigoSnippet = it },
    visualTransformation = CodeSyntaxVisualTransformation("Kotlin"), // o "SQL", "Bash", etc.
    textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = CodeText
    ),
    colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = CodeBackground,
        unfocusedContainerColor = CodeBackground,
        focusedBorderColor = PrimaryCyan,
        unfocusedBorderColor = OutlineDark,
        focusedTextColor = CodeText,
        unfocusedTextColor = CodeText
    ),
    modifier = Modifier.fillMaxWidth()
)
```

---

### Caso B: En vistas de solo lectura (Tarjetas o Pantalla de Detalle con `Text`)
Para formatear un `String` y mostrarlo coloreado dentro de un componente `Text`, puedes usar el método `filter` de la clase:

```kotlin
import androidx.compose.material3.Text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.devvault.app.ui.add_snippet.CodeSyntaxVisualTransformation
import com.devvault.app.ui.theme.CodeText

val visualTransformer = remember { CodeSyntaxVisualTransformation() }
val annotatedCode = remember(snippet.contenido) {
    visualTransformer.filter(AnnotatedString(snippet.contenido)).text
}

Text(
    text = annotatedCode,
    style = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = CodeText
    )
)
```

---

## 4. Buenas Prácticas al Manejar el Código
1. **Espaciados e Indentación**: Usa siempre `fontFamily = FontFamily.Monospace` para garantizar que los espacios tabulares tengan un ancho regular y no se desfiguren.
2. **Integridad de Datos**: Nunca apliques `.trim()` destructivo a bloques de código multinivel al guardarlos o copiarlos; guarda siempre el `String` crudo para preservar sangrías y retornos de carro `\n`.
