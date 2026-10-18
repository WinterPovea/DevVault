# 📘 GUÍA MAESTRA DE ESTUDIO Y DEFENSA TÉCNICA: DEVVAULT
**Asignatura:** Desarrollo de Aplicaciones Móviles (Nivel 7)  
**Docente:** Edgardo Panchana  
**Rama evaluada:** `integration/optimizacion`  
**Aplicación:** DevVault (Gestor Offline-First de Snippets y Comandos de Código)

---

# 1. Visión general

### ¿Qué es DevVault y qué problema resuelve?
DevVault es una aplicación móvil nativa Android diseñada para desarrolladores que necesitan almacenar, organizar y consultar fragmentos de código (*snippets*), scripts y comandos de consola de forma inmediata. Resuelve la pérdida de productividad provocada por comandos olvidados o dependencias de conexión a internet, garantizando disponibilidad absoluta bajo una arquitectura local reactiva (*Offline-First*).

### Stack tecnológico y versiones reales (`gradle/libs.versions.toml`)
- **Android Gradle Plugin (AGP):** `9.3.3`
- **Kotlin:** `2.2.10` (con KSP `2.3.10` y Kotlinx Serialization `1.9.0`)
- **Jetpack Compose BOM:** `2026.09.00` (Material Design 3 puro)
- **Room Database:** `2.8.5` (con extensiones `room-ktx`)
- **Dagger Hilt:** `2.60` (con `androidx.hilt:hilt-navigation-compose:1.4.0`)
- **Navigation Compose:** `2.10.2`
- **Lifecycle Runtime KTX:** `2.11.0` (soporte de `collectAsStateWithLifecycle`)
- **Target SDK / Min SDK:** Compile/Target SDK `36`, Min SDK `26`

### Diagrama de arquitectura del flujo de datos
```text
[Compose UI (Screen)]  <--- Recibe UiState inmutable y envía lambdas (() -> Unit)
         ^
         |  collectAsStateWithLifecycle()  /  Channel.receiveAsFlow()
         v
[Container (Route)]    <--- Inyecta el ViewModel con hiltViewModel()
         ^
         |  StateFlow<UiState> (solo lectura)  /  viewModelScope
         v
    [ViewModel]        <--- Coordina presentación; consume y ejecuta UseCases
         ^
         |  Llama métodos suspend / Recibe Flow<T>
         v
[DevVaultRepository]  <--- Interfaz que actúa como Single Source of Truth
         ^
         |  Implementado por DevVaultRepositoryImpl
         v
   [Room DAOs]         <--- SnippetDao y CategoriaDao (Flow de lectura, suspend de escritura)
         ^
         |  SQLite / Room Driver
         v
[DevVaultDatabase]     <--- Base de datos local persistente (SQLite cifrada/estructurada)
```

### Árbol de carpetas real de `com.devvault.app`
- `data/`: Acceso a datos, base de datos local Room, entidades, DAOs y repositorios.
  - `data/local/dao/`: Interfaces `SnippetDao` y `CategoriaDao` con consultas SQL.
  - `data/local/database/`: Clase abstracta `DevVaultDatabase` y el sembrador inicial `DatabaseSeeder`.
  - `data/local/entity/`: Entidades Room con anotaciones `@Entity` (`SnippetEntity` y `CategoriaEntity`).
  - `data/repository/`: Interfaz `DevVaultRepository` e implementación `DevVaultRepositoryImpl`.
- `di/`: Módulos de inyección de dependencias con Hilt (`DatabaseModule` y `RepositoryModule`).
- `domain/`: Capa de dominio con lógica de negocio desacoplada de frameworks.
  - `domain/usecase/`: Casos de uso (`ValidarSnippetUseCase`).
- `navigation/`: Grafo de navegación tipado (`DevVaultRoute` y `DevVaultNavHost`).
- `ui/`: Capa de presentación implementada 100% en Jetpack Compose.
  - `ui/add_snippet/`: Pantalla de creación de snippets, visual transformation de sintaxis y mappers.
  - `ui/categoria/`: Pantalla de detalle de una categoría con sus snippets específicos.
  - `ui/categorias/`: Catálogo general de categorías, selección múltiple y edición.
  - `ui/components/`: Componentes UI reutilizables (`SearchBar`, `DeleteConfirmDialog`, `StateViews`, `Tarjetas`).
  - `ui/favoritos/`: Pantalla de snippets destacados ordenados cronológicamente.
  - `ui/home/`: Pantalla principal con snippets recientes y buscador general.
  - `ui/model/`: Modelos de presentación desacoplados (`SnippetUi` y `CategoriaUi`).
  - `ui/snippet_detail/`: Pantalla de detalle individual, copiado y eliminación de snippet.
  - `ui/theme/`: Tokens de diseño M3 (`Color.kt`, `Theme.kt`, `Type.kt`, `CategoryColors.kt`).
- `util/`: Utilidades del sistema (`ClipboardManager.kt` para copiado exacto sin trim).

---

# 2. Cumplimiento del trabajo autónomo

| Requisito de la Rúbrica | Puntos | Estado | Cómo se cumple en DevVault | Archivo y línea de evidencia |
|---|:---:|:---:|---|---|
| **1. UI & State Hoisting** | 2.5 | **Cumple** | 100% Jetpack Compose + Material 3. Cada pantalla divide `XxxRoute` (inyecta ViewModel) de `XxxScreen` (stateless). Subcomponentes reciben solo estado inmutable y lambdas. `collectAsStateWithLifecycle()` exclusivo. | `HomeScreen.kt:46, 73`<br>`AddSnippetScreen.kt:50, 105`<br>`FavoritosScreen.kt:33, 66`<br>`CategoriasScreen.kt:75, 105`<br>`CategoriaDetailScreen.kt:49, 78`<br>`SnippetDetailScreen.kt:39, 80` |
| **2. MVVM & Estado Reactivo** | 2.0 | **Cumple** | Un `@HiltViewModel` por pantalla. Exposición de un único `StateFlow<UiState>` sellado (`Loading`, `Success`, `Error`). Cero `MutableStateFlow` públicos. Eventos puntuales mediante `Channel` + `receiveAsFlow()`. | `HomeViewModel.kt:23, 30`<br>`AddSnippetViewModel.kt:26, 33`<br>`FavoritosViewModel.kt:39, 49`<br>`CategoriasViewModel.kt:26, 37`<br>`CategoriaDetailViewModel.kt:30, 47`<br>`SnippetDetailViewModel.kt:27, 36` |
| **3. Room (Single Source of Truth)** | 2.5 | **Cumple** | Persistencia SQLite con Room. Consultas de lectura retornan `Flow<T>`, escrituras en `suspend fun`. Cero bloqueos de hilo (`runBlocking` ausente). Cero queries en hilo principal. | `SnippetDao.kt:16-35, 38-44`<br>`CategoriaDao.kt:16-19, 22-28`<br>`DatabaseModule.kt:28` |
| **4. Repositorio y Desacoplamiento** | 1.5 | **Cumple** | Contrato `DevVaultRepository` e implementación `DevVaultRepositoryImpl`. UI totalmente desacoplada de Room usando `SnippetUi` y `CategoriaUi` mediante mappers por feature. | `DevVaultRepository.kt:8-25`<br>`DevVaultRepositoryImpl.kt:10-40`<br>`SnippetUi.kt:3`<br>`CategoriaUi.kt:3` |
| **5. Navegación y Hilt (DI)** | 1.5 | **Cumple** | `NavHost` centralizado con rutas tipadas `@Serializable` en `DevVaultRoute`. Sin strings mágicos. Cero `PlaceholderScreen`. Inyección total con Hilt en App, Activity y ViewModels. | `DevVaultRoute.kt:7-24`<br>`DevVaultNavHost.kt:20-70`<br>`DevVaultApplication.kt:6`<br>`MainActivity.kt:10` |
| **Extra: Ícono Adaptativo Vectorial** | +0.3 | **Cumple** | Ícono adaptativo vectorial XML propio con bóveda de seguridad y símbolo `</>`, paleta de marca y configuración en `mipmap-anydpi-v26`. | `ic_launcher_foreground.xml:1`<br>`ic_launcher_background.xml:1`<br>`mipmap-anydpi-v26/ic_launcher.xml:1` |
| **Extra: Use Case de Dominio** | +0.3 | **Cumple** | `ValidarSnippetUseCase` con `operator fun invoke()`, validaciones reales de longitud y campos vacíos, probado con tests unitarios e inyectado en `AddSnippetViewModel`. | `ValidarSnippetUseCase.kt:5`<br>`ValidarSnippetUseCaseTest.kt:15`<br>`AddSnippetViewModel.kt:27, 115` |
| **Extra: WindowSizeClass** | +0.4 | **Parcial** *(No cumple API literal)* | Implementa layout responsivo reorganizando la grilla según ancho de pantalla mediante `LocalConfiguration.current.screenWidthDp`, pero no usa la clase formal `calculateWindowSizeClass()`. | `HomeScreen.kt:122`<br>`CategoriasScreen.kt:210` |

### Puntos débiles conocidos y cómo defenderlos ante el docente
1. **Pregunta docente:** *"¿Por qué implementaron el diseño adaptativo con `LocalConfiguration` y no con la dependencia `material3-window-size-class`?"*  
   **Respuesta del estudiante:** *"Por restricción arquitectónica estricta del proyecto de no alterar el catálogo de dependencias `libs.versions.toml`. Optamos por resolver el comportamiento visual adaptativo de forma nativa en Compose usando `LocalConfiguration.current.screenWidthDp`, reconfigurando dinámicamente las columnas de la grilla (2 en móvil, 3 en tablets compactas y 4 en pantallas expandidas)."*
2. **Pregunta docente:** *"¿Por qué algunos ViewModels importan `SnippetEntity` si la UI usa `SnippetUi`?"*  
   **Respuesta del estudiante:** *"Porque el ViewModel se sitúa en la frontera entre la capa de datos y la capa de presentación. La vista Compose y el `UiState` consumen estrictamente `SnippetUi` (desacoplado), y el ViewModel utiliza funciones de extensión del mapper (`toEntity()`) para comunicarse con el repositorio y Room."*

---

# 3. Capas transversales

### 3.1 Room Database
- **Explicación sencilla:** Es la base de datos interna del teléfono. Guarda los snippets y carpetas de forma permanente en un archivo SQLite para que la app funcione sin internet. Si se borra una categoría, se eliminan automáticamente sus snippets hijos gracias a la eliminación en cascada.
- **Explicación técnica:** Implementa SQLite mediante Room `2.8.5`. `SnippetEntity` declara una clave foránea hacia `CategoriaEntity` con `onDelete = ForeignKey.CASCADE`. Las lecturas emiten un `Flow<List<T>>` que notifica cambios reactivos automáticamente cuando ocurre cualquier mutación ejecutada mediante `suspend fun`.
- **Archivos:** [`SnippetEntity.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/data/local/entity/SnippetEntity.kt), [`CategoriaEntity.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/data/local/entity/CategoriaEntity.kt), [`SnippetDao.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/data/local/dao/SnippetDao.kt), [`CategoriaDao.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/data/local/dao/CategoriaDao.kt), [`DevVaultDatabase.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/data/local/database/DevVaultDatabase.kt).
- **Pregunta docente:** *¿Qué ventaja técnica ofrece que el DAO retorne un `Flow` en vez de un `List` directo?*  
  **Respuesta:** *El `Flow` es reactivo: Room observa la tabla SQLite y, ante cualquier `INSERT` o `UPDATE`, reemite automáticamente la lista actualizada al StateFlow del ViewModel sin necesidad de refrescar la pantalla manualmente.*

### 3.2 Repositorio
- **Explicación sencilla:** Es el intermediario entre la base de datos y la interfaz gráfica. Oculta cómo y de dónde se obtienen los datos.
- **Explicación técnica:** Aplica el patrón Repository mediante la interfaz `DevVaultRepository` y la implementación `DevVaultRepositoryImpl`. Actúa como *Single Source of Truth* abstrayendo los DAOs de Room para que la lógica de presentación dependa de abstracciones y no de implementaciones concretas.
- **Archivos:** [`DevVaultRepository.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/data/repository/DevVaultRepository.kt), [`DevVaultRepositoryImpl.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/data/repository/DevVaultRepositoryImpl.kt).
- **Pregunta docente:** *¿Por qué el repositorio debe definirse como una interfaz?*  
  **Respuesta:** *Permite cumplir el principio de inversión de dependencias (DIP) y desacoplar la capa de datos, facilitando crear mocks o fakes en pruebas unitarias sin tocar SQLite.*

### 3.3 Inyección de Dependencias con Hilt
- **Explicación sencilla:** Es una herramienta que construye y entrega automáticamente los objetos que cada clase necesita para funcionar, sin tener que crearlos a mano con `new`.
- **Explicación técnica:** Hilt `2.60` gestiona el árbol de dependencias con anotaciones en tiempo de compilación. `DevVaultApplication` (`@HiltAndroidApp`) inicializa el contenedor global; `MainActivity` (`@AndroidEntryPoint`) habilita la inyección en vistas; `DatabaseModule` provee la base de datos y los DAOs como `@Singleton`; y `RepositoryModule` vincula la interfaz `DevVaultRepository` a su implementación mediante `@Binds`.
- **Archivos:** [`DevVaultApplication.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/DevVaultApplication.kt), [`MainActivity.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/MainActivity.kt), [`DatabaseModule.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/di/DatabaseModule.kt), [`RepositoryModule.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/di/RepositoryModule.kt).
- **Pregunta docente:** *¿Qué diferencia existe entre `@Provides` y `@Binds` en Hilt?*  
  **Respuesta:** *`@Provides` se usa en clases de librerías externas o donde se requiere lógica de construcción (como `Room.databaseBuilder`); `@Binds` es más eficiente porque solo enlaza una interfaz con su clase implementadora sin generar código ejecutable adicional.*

### 3.4 Navegación Tipada
- **Explicación sencilla:** Es el sistema que cambia de pantalla en la aplicación utilizando rutas seguras y argumentos tipados en vez de cadenas de texto escritas a mano.
- **Explicación técnica:** Emplea `Navigation Compose 2.10.2` junto con Kotlinx Serialization. Las rutas se definen como objetos y clases selladas con `@Serializable` (`DevVaultRoute.Home`, `DevVaultRoute.CategoriaDetail(val categoriaId: Long)`). El NavHost mapea cada ruta a su composable `Route`.
- **Archivos:** [`DevVaultRoute.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/navigation/DevVaultRoute.kt), [`DevVaultNavHost.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/navigation/DevVaultNavHost.kt).
- **Pregunta docente:** *¿Qué problema previenen las rutas `@Serializable` frente a las URLs tradicionales tipo `"categoria/{id}"`?*  
  **Respuesta:** *Eliminan errores de tipografía en strings y permiten verificación de tipos en tiempo de compilación, evitando crashes en runtime por parámetros faltantes o nulos.*

### 3.5 Patrón Route/Screen y State Hoisting
- **Explicación sencilla:** Cada pantalla se divide en dos: la "Route" (que tiene el cerebro y habla con el ViewModel) y la "Screen" (que solo dibuja en pantalla lo que le pasan y avisa cuando tocas un botón).
- **Explicación técnica:** Implementa *State Hoisting* separando el contenedor con estado (*Stateful Composable* = `XxxRoute`) de la vista sin estado (*Stateless Composable* = `XxxScreen`). Los composables hijos nunca reciben el ViewModel; reciben un `UiState` inmutable y lambdas `() -> Unit` para propagar eventos hacia arriba.
- **Archivos:** Todos los archivos `*Screen.kt` en `ui/`.
- **Pregunta docente:** *¿Por qué está estrictamente prohibido pasar un ViewModel a un composable hijo o tarjeta?*  
  **Respuesta:** *Porque acopla el componente gráfico al ciclo de vida del ViewModel, rompe la previsualización (`@Preview`), impide reutilizar el componente en otras pantallas y dificulta las pruebas visuales.*

### 3.6 UiState sellado y StateFlow reactivo
- **Explicación sencilla:** La pantalla solo puede estar en 3 estados claros y definidos: Cargando (`Loading`), Mostrando datos (`Success`) o Mostrando error (`Error`).
- **Explicación técnica:** Cada ViewModel expone un único `StateFlow<XxxUiState>` de solo lectura (respaldado por un `MutableStateFlow` privado). `XxxUiState` es una `sealed interface` exhaustiva. La vista lo consume mediante `collectAsStateWithLifecycle()`, pausando la recolección si la app entra en segundo plano.
- **Archivos:** `HomeUiState.kt`, `AddSnippetUiState.kt`, `FavoritosUiState.kt`, `CategoriasUiState.kt`, `CategoriaDetailUiState.kt`, `SnippetDetailUiState.kt`.
- **Pregunta docente:** *¿Por qué usar `collectAsStateWithLifecycle()` en vez de `collectAsState()`?*  
  **Respuesta:** *Porque `collectAsStateWithLifecycle` detiene activamente la recolección de corrutinas cuando la vista no está visible (en estado `STOPPED`), ahorrando batería y CPU.*

### 3.7 Eventos Puntuales (One-off Events)
- **Explicación sencilla:** Cosas que solo deben pasar una vez, como mostrar un mensaje temporal (Snackbar o Toast) o regresar de pantalla. Si giras el teléfono, no se deben volver a mostrar.
- **Explicación técnica:** Se modelan con `Channel<UiEvent>(Channel.BUFFERED)` expuesto como `Flow` mediante `receiveAsFlow()`. En la `Route`, se consumen mediante `LaunchedEffect(Unit)` para asegurar que no se reejecuten ante recomposiciones.
- **Archivos:** `AddSnippetViewModel.kt:33-34`, `FavoritosViewModel.kt:44-45`, `SnippetDetailViewModel.kt:36-37`.
- **Pregunta docente:** *¿Por qué no se debe usar un booleano en el `StateFlow` para mostrar un Toast?*  
  **Respuesta:** *Porque los StateFlow conservan el último valor emitido. Si ocurre una recomposición o cambio de configuración (rotar pantalla), el Toast volvería a dispararse erróneamente.*

### 3.8 Mappers y Modelos de UI
- **Explicación sencilla:** Los datos guardados en la base de datos se traducen a un formato especial para la pantalla antes de mostrarlos, para no mezclar código de base de datos con botones y listas.
- **Explicación técnica:** Desacopla la capa de datos de la capa de presentación. Las entidades Room (`SnippetEntity`, `CategoriaEntity`) se mapean a modelos de UI (`SnippetUi`, `CategoriaUi`) en la carpeta `mapper/` de cada feature mediante funciones de extensión (`toUi()`, `toEntity()`).
- **Archivos:** [`SnippetUi.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/model/SnippetUi.kt), [`CategoriaUi.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/model/CategoriaUi.kt), y carpetas `ui/**/mapper/`.
- **Pregunta docente:** *¿Qué ventaja aporta tener `SnippetUi` si tiene campos similares a `SnippetEntity`?*  
  **Respuesta:** *Permite incorporar propiedades de formato específicas para la UI (como colores ya resueltos o nombres de categoría combinados) sin ensuciar la entidad de la base de datos relacional.*

### 3.9 Portapapeles (Copia Exacta)
- **Explicación sencilla:** Copia el código tal cual fue escrito, respetando espacios, saltos de línea y sangrías sin recortar nada.
- **Explicación técnica:** Utilidad en [`ClipboardManager.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/util/ClipboardManager.kt) que accede al servicio `android.content.ClipboardManager` inyectando `ClipData.newPlainText("snippet", texto)` puro, sin funciones de recorte (`trim()`) ni alteraciones de caracteres.
- **Archivos:** [`ClipboardManager.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/util/ClipboardManager.kt).
- **Pregunta docente:** *¿Por qué no debe aplicarse `.trim()` al copiar un snippet de código?*  
  **Respuesta:** *Porque en lenguajes como Python o en comandos YAML/Bash, los espacios iniciales y saltos de línea son sintaxis obligatoria; aplicar trim rompería la ejecución del código.*

### 3.10 Capa de Dominio / Use Cases
- **Explicación sencilla:** Es una clase encargada de verificar que los datos cumplan las reglas de negocio antes de guardarlos.
- **Explicación técnica:** Clase [`ValidarSnippetUseCase`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/domain/usecase/ValidarSnippetUseCase.kt) con `operator fun invoke()`. Valida que el título y contenido no estén vacíos y que el título no exceda 100 caracteres. Se inyecta en `AddSnippetViewModel` antes de llamar al repositorio.
- **Archivos:** [`ValidarSnippetUseCase.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/domain/usecase/ValidarSnippetUseCase.kt), [`ValidarSnippetUseCaseTest.kt`](file:///d:/Datos/Documentos/DevVault/app/src/test/java/com/devvault/app/domain/usecase/ValidarSnippetUseCaseTest.kt).
- **Pregunta docente:** *¿Por qué se usa `operator fun invoke` en un caso de uso?*  
  **Respuesta:** *Permite ejecutar la instancia del caso de uso como si fuera una función directa (`validarSnippetUseCase(titulo, contenido)`), otorgando una semántica limpia y concisa.*

### 3.11 Componentes Reutilizables y Tema
- **Explicación sencilla:** Bloques de construcción comunes (como la barra de búsqueda, las ventanas de confirmación y los avisos de error) compartidos por toda la app para no repetir código.
- **Explicación técnica:** Componentes stateless desacoplados en `ui/components/`:
  - `SearchBar.kt`: Campo de búsqueda con icono y borrado rápido.
  - `DeleteConfirmDialog.kt`: Modal destructivo con advertencia de cascada FK.
  - `StateViews.kt`: `LoadingView`, `EmptyView` y `ErrorView`.
  - `Tarjetas.kt`: `SnippetCard` y `CategoriaCard`.
  - `CategoryColors.kt`: Paleta `CategoryPalette` y función `resolverColorCategoria(Long)`.
- **Archivos:** Directorio [`ui/components/`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/components/) y [`ui/theme/`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/theme/).

### 3.12 Ícono y Diseño Adaptativo
- **Explicación sencilla:** El icono de la app se dibuja con vectores nítidos que no se pixelan. Además, la pantalla ajusta sus columnas si se abre en una tablet o modo horizontal.
- **Explicación técnica:**
  - Ícono adaptativo XML en capas: fondo `#141A2B` con círculo `#1E2538` (`ic_launcher_background.xml`) y silueta vectorial de bóveda con corchetes de código `</>` en `#33C8F2` (`ic_launcher_foreground.xml`), configurado en `mipmap-anydpi-v26`.
  - Diseño responsivo en `HomeScreen` y `CategoriasScreen`: detecta `LocalConfiguration.current.screenWidthDp` y conmuta el `LazyVerticalGrid` a 2 columnas en pantallas compactas (<600dp), 3 columnas en intermedias y 4 columnas en expandidas (>=840dp).

---

# 4. Módulos de pantalla

---

## 4.1 Módulo: Home (Inicio)

### 4.1.1 Qué ve y qué puede hacer el usuario
- Visualiza el encabezado corporativo "DEVVAULT" con saludo.
- Barra de búsqueda reactiva para filtrar snippets en tiempo real.
- Lista de los 5 snippets más recientes o lista completa de coincidencias de búsqueda.
- Estados visuales: carga (`LoadingView`), vacío (`EmptyView`) o error (`ErrorView`).
- Clic en cualquier fila para navegar al detalle del snippet.

### 4.1.2 Explicación sencilla (para entender)
Es la pantalla de bienvenida. Muestra rápidamente los últimos comandos que guardaste para que los encuentres sin buscar. Si escribes en el buscador, filtra al instante. Si tocas un comando, se abre su información completa.

### 4.1.3 Explicación técnica (recorrido real)
1. El usuario teclea en `SearchBar` → lanza lambda `onEvent(HomeEvent.OnSearchQueryChange(query))`.
2. `HomeViewModel` actualiza `_searchQuery.value`.
3. El operador `.debounce(300.milliseconds)` retarda la emisión y `.flatMapLatest` conmuta:
   - Si query tiene texto: llama a `repository.buscarSnippets(query)`.
   - Si query está vacía: llama a `repository.obtenerSnippets().map { it.take(5) }`.
4. El DAO ejecuta la consulta reactiva `SELECT * FROM snippets...` y emite `Flow<List<SnippetEntity>>`.
5. El operador `combine` emite `HomeUiState.Success(searchQuery, snippets.toUiList())`.
6. `HomeScreen` recolecta con `collectAsStateWithLifecycle()` y recomone la lista.

### 4.1.4 Archivos del módulo
- [`HomeUiState.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/home/HomeUiState.kt)
- [`HomeViewModel.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/home/HomeViewModel.kt)
- [`HomeScreen.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/home/HomeScreen.kt)
- [`HomeHeader.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/home/components/HomeHeader.kt)
- [`SnippetListItem.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/home/components/SnippetListItem.kt)
- [`SnippetUiMapper.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/home/mapper/SnippetUiMapper.kt)

### 4.1.5 Estados y eventos
- `HomeUiState.Loading`: Indicador circular centrado.
- `HomeUiState.Success`: `searchQuery: String`, `snippets: List<SnippetUi>`.
- `HomeUiState.Error`: `mensaje: String`.
- `HomeEvent`: `OnSearchQueryChange`, `OnSnippetClick`, `OnClearSearch`.

### 4.1.6 Cumplimiento de rúbrica en este módulo
- UI/Hoisting: `HomeRoute` inyecta ViewModel; `HomeScreen` recibe solo `uiState` y `onEvent`.
- MVVM: `StateFlow<HomeUiState>` sellado, `_searchQuery` privado.
- Room: Reactividad directa con `Flow`.
- Desacoplamiento: Emite `SnippetUi`, cero entidades Room en el Composable.

### 4.1.7 Fragmento de código clave
```kotlin
// HomeViewModel.kt:30-45 - Debounce y recolección reactiva
val uiState: StateFlow<HomeUiState> = combine(
    _searchQuery,
    _searchQuery.debounce(300.milliseconds).flatMapLatest { query ->
        if (query.trim().isNotEmpty()) {
            repository.buscarSnippets(query.trim())
        } else {
            repository.obtenerSnippets().map { it.take(5) }
        }
    }
) { currentQuery, snippetsResult ->
    HomeUiState.Success(searchQuery = currentQuery, snippets = snippetsResult.toUiList())
}.catch { emit(HomeUiState.Error(it.message ?: "Error")) }
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), HomeUiState.Loading)
```

### 4.1.8 Cómo mostrarlo en la demo (25 segundos)
1. Abrir la app: mostrar los 5 comandos recientes cargados al instante desde Room.
2. Escribir "docker" en la barra de búsqueda: evidenciar el filtrado dinámico en tiempo real.
3. Presionar la "X" del buscador para limpiar y volver a la lista de recientes.

### 4.1.9 Preguntas probables del docente
1. *¿Por qué usan `debounce(300.milliseconds)` en la búsqueda?*  
   **Respuesta:** Para evitar consultar Room con cada letra tecleada, esperando 300 ms de inactividad del usuario.
2. *¿Cómo limitan la lista de recientes a 5 elementos sin bloquear el hilo?*  
   **Respuesta:** Usando el operador reactivo de flujo `.map { it.take(5) }` dentro de la corrutina en segundo plano.
3. *¿Qué pasa si la consulta de búsqueda falla en SQLite?*  
   **Respuesta:** El operador `.catch` captura la excepción y emite `HomeUiState.Error` sin congelar la interfaz.

---

## 4.2 Módulo: Categorías

### 4.2.1 Qué ve y qué puede hacer el usuario
- Barra de búsqueda para filtrar categorías por nombre.
- Grilla adaptativa de categorías con inicial coloreada, nombre y total de snippets asociados.
- Clic simple: navega a ver los snippets de esa categoría.
- Clic largo / Selección: activa la barra contextual superior con botones Editar (lápiz) y Eliminar (basurero).
- Diálogo para renombrar categoría (`EditarCategoriaDialog`).
- Diálogo de eliminación con advertencia de borrado en cascada de snippets hijos (`DeleteConfirmDialog`).

### 4.2.2 Explicación sencilla (para entender)
Es el organizador por carpetas (ej. Docker, Git, SQL). Muestra cuántos comandos tiene cada carpeta. Si mantienes presionada una tarjeta, puedes cambiarle el nombre o borrarla junto con todos sus comandos guardados.

### 4.2.3 Explicación técnica (recorrido real)
1. `CategoriasViewModel` combina `obtenerCategorias()` y `obtenerSnippets()`.
2. Calcula los conteos agrupando con `snippets.groupingBy { it.categoriaId }.eachCount()`.
3. Mapea cada `CategoriaEntity` a `CategoriaUi(id, nombre, color, totalSnippets)`.
4. Si el usuario pulsa eliminar → `onEliminarCategoria(id)` lanza corrutina `repository.eliminarCategoriaPorId(id)`.
5. Room ejecuta el delete y activa `FOREIGN KEY CASCADE`, borrando en cascada los snippets vinculados.

### 4.2.4 Archivos del módulo
- [`CategoriasUiState.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/categorias/CategoriasUiState.kt)
- [`CategoriasViewModel.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/categorias/CategoriasViewModel.kt)
- [`CategoriasScreen.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/categorias/CategoriasScreen.kt)
- [`CategoriaUiMapper.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/categorias/mapper/CategoriaUiMapper.kt)
- Componentes: `SearchBar`, `CategoriaCard`, `DeleteConfirmDialog`.

### 4.2.5 Estados y eventos
- `CategoriasUiState.Loading`, `Success(categorias, busqueda, seleccionadaId)`, `Error(mensaje)`.
- Eventos: `Channel<CategoriasUiEvent>` para notificaciones.

### 4.2.6 Cumplimiento de rúbrica en este módulo
- Demuestra clave foránea con borrado en cascada (`ForeignKey.CASCADE`).
- Cálculo reactivo de totales combinando dos flujos de Room sin consultas N+1 en SQLite.
- Modo contextual desacoplado en Compose puro.

### 4.2.7 Fragmento de código clave
```kotlin
// CategoriasViewModel.kt:41-49 - Agrupación y cálculo reactivo
val conteos = snippets.groupingBy { it.categoriaId }.eachCount()
val filtradas = categorias
    .filter { it.nombre.contains(texto.trim(), ignoreCase = true) }
    .map { it.toUi(conteos[it.id] ?: 0) }
CategoriasUiState.Success(
    categorias = filtradas,
    busqueda = texto,
    seleccionadaId = seleccionada
)
```

### 4.2.8 Cómo mostrarlo en la demo (25 segundos)
1. Tocar la pestaña "Categorías": mostrar la cuadrícula de carpetas y el total de comandos en cada una.
2. Mantener presionada una categoría: mostrar la barra superior contextual azul/oscura.
3. Presionar editar, cambiarle el nombre y guardar: observar la reactividad inmediata.

### 4.2.9 Preguntas probables del docente
1. *¿Cómo calculan el total de snippets por categoría?*  
   **Respuesta:** En memoria con `groupingBy { it.categoriaId }.eachCount()` dentro del `combine`, evitando disparar múltiples consultas `COUNT()` a SQLite.
2. *¿Qué mecanismo previene borrar una categoría y dejar snippets huérfanos?*  
   **Respuesta:** La anotación `@ForeignKey(onDelete = ForeignKey.CASCADE)` en `SnippetEntity`, delegando la integridad referencial a SQLite.
3. *¿Cómo se maneja la selección contextual sin romper el State Hoisting?*  
   **Respuesta:** La UI emite el `id` seleccionado mediante la lambda `onToggleSeleccion` y el ViewModel actualiza `seleccionadaId` en el `UiState`.

---

## 4.3 Módulo: Detalle de Categoría

### 4.3.1 Qué ve y qué puede hacer el usuario
- Barra superior con botón de regreso y título de la categoría seleccionada.
- Lista completa de snippets pertenecientes a esa categoría.
- Tarjeta de snippet (`SnippetCard`) con botón de favorito (estrella), botón de copiado, editar y eliminar.
- Diálogo modal de edición para modificar título y código del snippet.

### 4.3.2 Explicación sencilla (para entender)
Es el interior de una carpeta. Al entrar a "Git", solo verás comandos de Git. Puedes copiar cualquier comando, marcarlo como favorito, cambiarle el texto o borrarlo.

### 4.3.3 Explicación técnica (recorrido real)
1. Recibe `categoriaId` vía `SavedStateHandle.toRoute<DevVaultRoute.CategoriaDetail>()`.
2. `CategoriaDetailViewModel` combina `obtenerCategorias()` y `obtenerSnippetsPorCategoria(categoriaId)`.
3. Al pulsar copiar → `onCopiar(texto)` invoca `copiarAlPortapapeles(context, texto)` y el ViewModel emite `CategoriaDetailUiEvent.ShowMessage` mediante `Channel`.
4. La `Route` captura el evento en `LaunchedEffect` y dispara un Toast.

### 4.3.4 Archivos del módulo
- [`CategoriaDetailUiState.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/categoria/CategoriaDetailUiState.kt)
- [`CategoriaDetailViewModel.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/categoria/CategoriaDetailViewModel.kt)
- [`CategoriaDetailScreen.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/categoria/CategoriaDetailScreen.kt)
- [`SnippetUiMapper.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/categoria/mapper/SnippetUiMapper.kt)

### 4.3.5 Estados y eventos
- `CategoriaDetailUiState.Loading`, `Success(nombreCategoria, snippets)`, `Error`.
- Eventos: `CategoriaDetailUiEvent.ShowMessage(message: String)`.

### 4.3.6 Cumplimiento de rúbrica en este módulo
- Paso seguro de argumentos entre rutas con Navigation Compose tipado (`categoriaId: Long`).
- Uso de `SnippetCard` reutilizable compartida con Favoritos.
- Modificación directa en Room mediante `suspend fun actualizarSnippet`.

### 4.3.7 Fragmento de código clave
```kotlin
// CategoriaDetailViewModel.kt:47-54 - Filtrado seguro por ID de ruta
combine(
    repository.obtenerCategorias(),
    repository.obtenerSnippetsPorCategoria(categoriaId)
) { categorias, snippets ->
    val categoria = categorias.firstOrNull { it.id == categoriaId }
    if (categoria == null) CategoriaDetailUiState.Error("Categoría no encontrada")
    else CategoriaDetailUiState.Success(categoria.nombre, snippets.toUiList(categoria.nombre, categoria.color))
}
```

### 4.3.8 Cómo mostrarlo en la demo (20 segundos)
1. Tocar la categoría "Docker": entrar a la pantalla de detalle.
2. Tocar el botón "⧉ Copiar" de un snippet: mostrar el Toast de confirmación de copiado.
3. Tocar la estrella: marcarlo como favorito al instante.

### 4.3.9 Preguntas probables del docente
1. *¿Cómo se obtiene el argumento `categoriaId` en el ViewModel?*  
   **Respuesta:** Mediante `SavedStateHandle.toRoute<DevVaultRoute.CategoriaDetail>().categoriaId` provisto por Navigation Compose.
2. *¿Por qué el toast se lanza con un Channel y no dentro de la función de copiado de Compose?*  
   **Respuesta:** Para desacoplar la lógica de eventos de la vista y garantizar que el evento se procese de forma unidireccional y una sola vez.
3. *¿Qué query en Room soporta esta pantalla?*  
   **Respuesta:** `@Query("SELECT * FROM snippets WHERE categoriaId = :categoriaId ORDER BY titulo ASC")`.

---

## 4.4 Módulo: Agregar Snippet

### 4.4.1 Qué ve y qué puede hacer el usuario
- Campo para el nombre/título del comando.
- Selector desplegable (Dropdown) con las categorías existentes.
- Botón "+" para crear una nueva categoría al vuelo con selector de color.
- Área de edición de código monoespaciada con resaltado de sintaxis en tiempo real (*Syntax Highlighting*).
- Botón de guardado con validación de campos y mensajes en Snackbar.

### 4.4.2 Explicación sencilla (para entender)
Es el formulario para guardar un nuevo comando. Escribes el nombre, eliges la categoría (o creas una nueva con su color), pegas el código (que se colorea automáticamente según el lenguaje) y guardas.

### 4.4.3 Explicación técnica (recorrido real)
1. El usuario presiona "Guardar snippet" → `AddSnippetViewModel.saveSnippet()`.
2. Invoca la capa de dominio: `validarSnippetUseCase(nombre, contenido)`.
3. Si la validación falla: actualiza mensajes de error en el `UiState` y emite `ShowSnackbar(msg)` vía `Channel`.
4. Si es válido: crea `SnippetEntity`, llama a `repository.insertarSnippet(entity)` en `viewModelScope`.
5. Room persiste el registro en SQLite y el ViewModel emite `AddSnippetUiEvent.NavigateBack`.
6. La `Route` ejecuta `navController.popBackStack()`.

### 4.4.4 Archivos del módulo
- [`AddSnippetUiState.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/add_snippet/AddSnippetUiState.kt)
- [`AddSnippetViewModel.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/add_snippet/AddSnippetViewModel.kt)
- [`AddSnippetScreen.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/add_snippet/AddSnippetScreen.kt)
- [`ValidarSnippetUseCase.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/domain/usecase/ValidarSnippetUseCase.kt)
- [`CodeSyntaxVisualTransformation.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/add_snippet/AddSnippetScreen.kt#L10) (Syntax Highlighting)

### 4.4.5 Estados y eventos
- `AddSnippetUiState.Loading`, `Success(...)`, `Error`.
- Eventos: `ShowSnackbar(message: String)`, `NavigateBack`.

### 4.4.6 Cumplimiento de rúbrica en este módulo
- Uso de `ValidarSnippetUseCase` con validación de negocio desacoplada.
- Resaltado sintáctico con `VisualTransformation` de Compose sin alterar el texto original subyacente.
- Inserción asíncrona segura con `suspend fun`.

### 4.4.7 Fragmento de código clave
```kotlin
// AddSnippetViewModel.kt:115-132 - Uso del caso de uso e inserción segura
val validationError = validarSnippetUseCase(state.nombre, state.contenido)
val categoriaError = if (state.selectedCategoriaId == null) "Se necesita ingresar una categoría" else null
if (validationError != null || categoriaError != null) {
    val errorMsg = categoriaError ?: validationError ?: "Datos inválidos"
    updateSuccess { it.copy(nombreError = nombreError, contenidoError = contenidoError, categoriaError = categoriaError) }
    viewModelScope.launch { _events.send(AddSnippetUiEvent.ShowSnackbar(errorMsg)) }
    return
}
```

### 4.4.8 Cómo mostrarlo en la demo (30 segundos)
1. Tocar el botón central "+" de la barra inferior.
2. Intentar guardar en blanco: mostrar cómo el `ValidarSnippetUseCase` activa el Snackbar de validación.
3. Llenar Título: "Docker stop all", Código: `docker stop $(docker ps -a -q)`, Categoría "Docker".
4. Guardar: mostrar cómo regresa automáticamente y el comando ya existe en la lista de Room.

### 4.4.9 Preguntas probables del docente
1. *¿Cómo funciona el coloreado de código en el campo de texto?*  
   **Respuesta:** Con un `VisualTransformation` que intercepta la cadena y genera un `AnnotatedString` con estilos de color M3 basados en expresiones regulares del lenguaje, sin mutar el texto real.
2. *¿Dónde se valida que el título no supere 100 caracteres?*  
   **Respuesta:** En la capa de dominio a través de `ValidarSnippetUseCase`, cumpliendo el principio de separación de responsabilidades.
3. *¿Por qué el autoguardado usa corrutinas?*  
   **Respuesta:** Para transferir la inserción I/O en SQLite fuera del hilo principal a través de los despachadores de Room.

---

## 4.5 Módulo: Favoritos

### 4.5.1 Qué ve y qué puede hacer el usuario
- Barra superior con botón volver y título "Favoritos".
- Buscador exclusivo para filtrar dentro de los favoritos.
- Lista de snippets marcados con estrella, ordenados por fecha en que fueron agregados a favoritos.
- Botón de estrella para remover de favoritos al instante.
- Botón para copiar código íntegro al portapapeles.

### 4.5.2 Explicación sencilla (para entender)
Es tu lista de comandos destacados. Todo lo que marques con una estrella aparece aquí para acceso rápido, ordenado desde el más reciente que marcaste.

### 4.5.3 Explicación técnica (recorrido real)
1. `FavoritosViewModel` escucha `repository.obtenerSnippetsFavoritos()`.
2. DAO ejecuta `@Query("SELECT * FROM snippets WHERE esFavorito = 1")`.
3. Combina la lista con `_searchQuery` y ordena descendentemente con `sortedByDescending { it.fechaFavorito ?: 0L }`.
4. Al alternar favorito: llama a `actualizarSnippet(copy(esFavorito = false, fechaFavorito = null))`.
5. Room actualiza SQLite y emite la nueva lista por el `Flow`, removiendo la tarjeta automáticamente.

### 4.5.4 Archivos del módulo
- [`FavoritosUiState.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/favoritos/FavoritosUiState.kt)
- [`FavoritosViewModel.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/favoritos/FavoritosViewModel.kt)
- [`FavoritosScreen.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/favoritos/FavoritosScreen.kt)
- [`SnippetUiMapper.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/favoritos/mapper/SnippetUiMapper.kt)

### 4.5.5 Estados y eventos
- `FavoritosUiState.Loading`, `Success(favoritos, searchQuery)`, `Error`.
- Eventos: `FavoritosUiEvent.ShowToast("Código copiado al portapapeles")`.

### 4.5.6 Cumplimiento de rúbrica en este módulo
- Manejo reactivo de estado en favoritos sin recarga manual.
- Búsqueda integrada como parte del estado sellado `Success`.
- Copia fiel al portapapeles con `ClipboardManager`.

### 4.5.7 Fragmento de código clave
```kotlin
// FavoritosViewModel.kt:47-66 - Orden cronológico y filtrado en StateFlow
val uiState: StateFlow<FavoritosUiState> = combine(
    repository.obtenerSnippetsFavoritos(),
    _searchQuery
) { favoritos, query ->
    val filtrados = if (query.isBlank()) favoritos
    else favoritos.filter { it.titulo.contains(query, true) || it.contenido.contains(query, true) }
    val ordenados = filtrados.sortedByDescending { it.fechaFavorito ?: 0L }
    FavoritosUiState.Success(favoritos = ordenados.map { it.toUi() }, searchQuery = query)
}.catch { emit(FavoritosUiState.Error(it.message ?: "Error")) }
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FavoritosUiState.Loading)
```

### 4.5.8 Cómo mostrarlo en la demo (20 segundos)
1. Ir a la pestaña "Favoritos": mostrar la lista de comandos destacados.
2. Desmarcar la estrella de un comando: ver cómo desaparece inmediatamente por la reactividad del `Flow`.
3. Probar el buscador de favoritos para encontrar un comando específico.

### 4.5.9 Preguntas probables del docente
1. *¿Cómo garantizan que los favoritos se ordenen por el momento en que se marcaron y no por creación?*  
   **Respuesta:** La entidad tiene el campo nullable `fechaFavorito: Long?` que almacena `System.currentTimeMillis()` al activarse y se ordena descendentemente.
2. *¿Por qué no usan dos `StateFlow` separados para la lista y el texto del buscador?*  
   **Respuesta:** Para evitar inconsistencias de estado (*race conditions*); un único `StateFlow<FavoritosUiState.Success>` agrupa tanto la lista filtrada como el texto actual.
3. *¿Qué ocurre si el usuario apaga el teléfono con snippets en favoritos?*  
   **Respuesta:** Persisten intactos en SQLite porque el estado `esFavorito = 1` y la marca de tiempo se guardan físicamente en Room.

---

## 4.6 Módulo: Detalle de Snippet

### 4.6.1 Qué ve y qué puede hacer el usuario
- Barra superior con botón volver y botón de estrella (favorito).
- Título completo del snippet en tipografía destacada.
- Badge con el nombre y color de la categoría/lenguaje.
- Bloque oscuro de código con Syntax Highlighting en fuente monoespaciada.
- Botón principal "Copiar comando".
- Botón "Eliminar snippet" con confirmación en diálogo modal.

### 4.6.2 Explicación sencilla (para entender)
Es la ficha técnica de un comando. Muestra el código en grande con sus colores, permite copiarlo en un toque y tiene la opción de eliminarlo si ya no te sirve.

### 4.6.3 Explicación técnica (recorrido real)
1. Recibe `snippetId` tipado desde `DevVaultRoute.SnippetDetail`.
2. `SnippetDetailViewModel` consulta el snippet y su categoría mediante `obtenerSnippetPorId(id)` y `obtenerCategoriaPorId(catId)`.
3. Si el usuario pulsa eliminar → invoca `repository.eliminarSnippet(entity)`.
4. Emite `SnippetDetailUiEvent.ShowToast("Snippet eliminado")` y `SnippetDetailUiEvent.NavigateBack`.
5. La vista intercepta el evento y regresa a la pantalla anterior.

### 4.6.4 Archivos del módulo
- [`SnippetDetailUiState.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/snippet_detail/SnippetDetailUiState.kt)
- [`SnippetDetailViewModel.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/snippet_detail/SnippetDetailViewModel.kt)
- [`SnippetDetailScreen.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/snippet_detail/SnippetDetailScreen.kt)
- [`SnippetUiMapper.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/ui/snippet_detail/mapper/SnippetUiMapper.kt)
- Componente: `DeleteConfirmDialog`.

### 4.6.5 Estados y eventos
- `SnippetDetailUiState.Loading`, `Success(snippet: SnippetUi, categoriaNombre)`, `Error(mensaje)`.
- Eventos: `ShowToast(message: String)`, `NavigateBack`.

### 4.6.6 Cumplimiento de rúbrica en este módulo
- Reemplazo completo y eliminación de cualquier `PlaceholderScreen`.
- Integración con el diálogo compartido `DeleteConfirmDialog`.
- Estado inmutable y lambdas hacia el Screen.

### 4.6.7 Fragmento de código clave
```kotlin
// SnippetDetailViewModel.kt:70-76 - Alternar favorito y feedback reactivo
fun alternarFavorito() {
    val current = _uiState.value as? SnippetDetailUiState.Success ?: return
    viewModelScope.launch {
        val nuevoFavorito = !current.snippet.esFavorito
        val updated = current.snippet.copy(esFavorito = nuevoFavorito, fechaFavorito = if (nuevoFavorito) System.currentTimeMillis() else null)
        repository.actualizarSnippet(updated.toEntity())
        _uiState.value = current.copy(snippet = updated)
        _events.send(SnippetDetailUiEvent.ShowToast(if (nuevoFavorito) "Añadido a favoritos" else "Eliminado de favoritos"))
    }
}
```

### 4.6.8 Cómo mostrarlo en la demo (20 segundos)
1. Tocar un snippet desde Home: entrar a su pantalla de detalle individual.
2. Tocar "Copiar comando": mostrar el Toast y pegar en cualquier app externa si se requiere.
3. Presionar "Eliminar snippet" y confirmar: ver cómo se elimina y retorna limpiamente a Home.

### 4.6.9 Preguntas probables del docente
1. *¿Cómo se resuelve el nombre de la categoría si el snippet solo tiene `categoriaId`?*  
   **Respuesta:** En el ViewModel se consulta `obtenerCategoriaPorId(snippet.categoriaId)` y se mapea dentro del estado `SnippetDetailUiState.Success`.
2. *¿Qué asegura que al eliminar se vuelva a la pantalla anterior?*  
   **Respuesta:** El canal emite el evento `SnippetDetailUiEvent.NavigateBack`, consumido por `LaunchedEffect` en la Route para ejecutar `navController.popBackStack()`.
3. *¿Por qué el código se muestra dentro de un `Surface` con `FontFamily.Monospace`?*  
   **Respuesta:** Para garantizar alineación precisa de caracteres tipográficos de programación y fondo diferenciado `#05070B` acorde al mockup de alta fidelidad.

---

# 5. Demostración offline-first (Guion paso a paso)

Este es el requisito obligatorio de la rúbrica del docente para la demostración en vivo.

### Paso 1: Registro de datos iniciales (Conexión normal)
1. Abrir la app DevVault en el emulador o teléfono.
2. Navegar a **"+" (Agregar Snippet)**.
3. Registrar un nuevo comando:
   - **Título:** `Docker Prune System`
   - **Categoría:** Seleccionar `Docker`
   - **Snippet:** `docker system prune -a --volumes`
4. Presionar **"Guardar snippet"**. Mostrar cómo aparece inmediatamente en la lista de Home y en la categoría Docker.

### Paso 2: Matar el proceso de la aplicación (*Kill Process*)
1. Minimizar la app o abrir la vista de multitarea del sistema Android.
2. Deslizar la tarjeta de DevVault hacia arriba para **forzar el cierre (matar el proceso)**.
3. *Explicación al docente:* *"En este momento la memoria RAM de la aplicación fue completamente destruida por el sistema operativo."*

### Paso 3: Activar Modo Avión (*Airplane Mode*)
1. Desplegar la barra de notificaciones del dispositivo Android.
2. Activar el **Modo Avión** (desconectando WiFi y datos móviles al 100%).
3. *Explicación al docente:* *"El dispositivo no cuenta con ningún tipo de conexión a red externa."*

### Paso 4: Reapertura y comprobación de persistencia
1. Abrir nuevamente **DevVault** desde el cajón de aplicaciones.
2. **Comprobar:** El comando `Docker Prune System` y todas las categorías aparecen al instante, intactos y reactivos.
3. Entrar a Favoritos y marcarlo con estrella en modo avión: la UI responde inmediatamente y los cambios persisten.

### ¿Qué parte del código hace posible este comportamiento?
- **Single Source of Truth en Room:** En [`DatabaseModule.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/di/DatabaseModule.kt), Room almacena la base de datos físicamente en el almacenamiento interno protegido del dispositivo (`devvault_database.db`).
- **Flujos reactivos fríos (`Flow`):** Los DAOs en [`SnippetDao.kt`](file:///d:/Datos/Documentos/DevVault/app/src/main/java/com/devvault/app/data/local/dao/SnippetDao.kt) leen directamente de SQLite sin depender de llamadas HTTP ni servidores intermedios.

---

# 6. Guion del Elevator Pitch Técnico (5 Minutos)

**Regla estricta del docente:** Duración máxima de 5 minutos cronometrados.

```text
[0:00 - 1:00]  MINUTO 1: Problema, Solución y Stack      ---> Integrante 1
[1:00 - 3:00]  MINUTOS 2 Y 3: Demostración en Vivo       ---> Integrante 2 y 3
[3:00 - 5:00]  MINUTOS 4 Y 5: Arquitectura y Código     ---> Integrante 4 y 5
```

### Minuto 1: Problema y Solución (Integrante 1)
> *"Buenos días docente y compañeros. Como desarrolladores, perdemos valioso tiempo buscando comandos complejos de consola, scripts de Docker o consultas SQL que usamos esporádicamente. DevVault resuelve esto como un gestor de código nativo, seguro y 100% offline-first. Desarrollamos la aplicación con el estándar Modern Android Development: Jetpack Compose con Material 3, arquitectura MVVM limpia, inyección de dependencias con Hilt y Room Database como Única Fuente de Verdad. A continuación, veremos la demostración en vivo."*

### Minutos 2 y 3: Live Demo y Prueba Offline-First (Integrantes 2 y 3)
- **Integrante 2 (1:00 - 2:00):**
  > *"Aquí vemos la interfaz de DevVault. En Home tenemos los comandos recientes y un buscador con debounce en tiempo real. Entramos a Categorías, donde vemos las tarjetas calculadas reactivamente. Seleccionamos Docker y agregamos un nuevo snippet llamado 'Docker System Prune'. El editor cuenta con resaltado sintáctico monoespaciado en tiempo real. Guardamos y el snippet se almacena inmediatamente en SQLite."*
- **Integrante 3 (2:00 - 3:00):**
  > *"Ahora demostramos el requisito offline-first obligatorio: cerramos y matamos el proceso de la aplicación en la multitarea de Android. Activamos el Modo Avión en el dispositivo. Volvemos a abrir DevVault: la aplicación carga de inmediato desde Room sin dependencias de red. Marcamos el snippet como favorito y la reactividad actualiza la base de datos local al instante."*

### Minutos 4 y 5: Arquitectura y Código Fuente (Integrantes 4 y 5)
- **Integrante 4 (3:00 - 4:00) — UI & State Hoisting:**
  > *"A nivel de UI, aplicamos State Hoisting estricto: separamos cada pantalla en un composable contenedor con estado, como HomeRoute, que inyecta el ViewModel mediante Hilt, y una vista pura stateless, HomeScreen. Ningún subcomponente recibe instancias de ViewModel; únicamente reciben estados inmutables y lambdas. Consumimos el StateFlow con `collectAsStateWithLifecycle` para pausar la recolección si la app va a segundo plano."*
- **Integrante 5 (4:00 - 5:00) — Room, DI y Dominio:**
  > *"En la capa de datos, Room es nuestra Single Source of Truth. Los DAOs retornan `Flow` para lecturas reactivas y `suspend fun` para escrituras fuera del hilo principal. Todo está inyectado con Hilt a través de interfaces, garantizando que el ViewModel nunca instancie DAOs directamente. Además, desacoplamos las entidades de Room mediante modelos `SnippetUi` y creamos un caso de uso puro `ValidarSnippetUseCase` con `operator fun invoke` para la lógica de validación de negocio. Con esto concluimos y quedamos listos para sus preguntas."*

---

# 7. Glosario Técnico

1. **StateFlow:** Flujo de datos observable que retiene el último estado emitido y lo transmite de forma reactiva y segura a los suscriptores (`HomeViewModel.kt:30`).
2. **Flow:** Flujo asíncrono frío de corrutinas que emite valores de forma secuencial desde Room hasta la capa de presentación (`SnippetDao.kt:16`).
3. **suspend fun:** Función de suspensión en Kotlin que puede pausarse y reanudarse sin bloquear el hilo donde se ejecuta (`SnippetDao.kt:38`).
4. **Stateful Composable (Route):** Composable contenedor responsable de inyectar el ViewModel, suscribirse a eventos de ciclo de vida y gestionar efectos secundarios (`HomeScreen.kt:43`).
5. **Stateless Composable (Screen):** Composable puro que no maneja dependencias directas ni estado mutable; solo recibe datos inmutables y emite eventos vía lambdas (`HomeScreen.kt:73`).
6. **State Hoisting:** Patrón de diseño en Compose donde el estado se eleva al llamador para hacer que los componentes sean reutilizables y fáciles de probar.
7. **DAO (Data Access Object):** Interfaz que define las operaciones y consultas SQL mapeadas a métodos en Room (`CategoriaDao.kt:13`).
8. **Entity:** Clase de datos en Kotlin anotada con `@Entity` que define la estructura de una tabla relacional en SQLite (`SnippetEntity.kt:22`).
9. **Single Source of Truth (SSOT):** Principio arquitectónico donde la base de datos local (Room) es la única fuente autorizada de datos para toda la aplicación.
10. **Repository:** Capa de abstracción que unifica el acceso a datos y aísla a los ViewModels de los detalles concretos de la base de datos (`DevVaultRepository.kt:8`).
11. **Dagger Hilt:** Framework de inyección de dependencias para Android basado en Dagger que gestiona el ciclo de vida de los componentes (`DatabaseModule.kt:15`).
12. **`@Inject constructor`:** Anotación que le indica a Hilt cómo instanciar una clase inyectando automáticamente sus dependencias (`DevVaultRepositoryImpl.kt:10`).
13. **Sealed Interface:** Interfaz sellada de Kotlin que restringe su jerarquía de clases hijas, permitiendo comprobaciones exhaustivas en bloques `when` (`HomeUiState.kt:5`).
14. **Mapper:** Función de transformación que convierte modelos de base de datos en modelos específicos para la interfaz gráfica (`SnippetUiMapper.kt:6`).
15. **Channel:** Primitiva de corrutinas para comunicación entre hilos tipo cola (*pipeline*), ideal para eventos de un solo uso como Toasts (`FavoritosViewModel.kt:44`).
16. **Recomposición:** Proceso de Jetpack Compose donde los composables se vuelven a ejecutar para actualizar la UI cuando su estado observado cambia.
17. **ForeignKey.CASCADE:** Regla de integridad en SQLite que elimina automáticamente todos los registros dependientes cuando se borra el registro padre (`SnippetEntity.kt:15`).

---

# 8. Banco de Preguntas de Repaso para la Defensa

### Capa de UI y State Hoisting
1. *¿Por qué no pasan el ViewModel a los composables hijos? (Pregunta trampa)*  
   **R:** Porque violaría el principio de State Hoisting, impediría previsualizar componentes con `@Preview`, acoplaría la UI al ciclo de vida del ViewModel y destruiría la reusabilidad del componente.
2. *¿Para qué sirve `collectAsStateWithLifecycle()`?*  
   **R:** Para recolectar flujos de Compose teniendo en cuenta el ciclo de vida del Activity/Fragment, deteniendo el consumo de recursos cuando la app no está visible.
3. *¿Qué diferencia hay entre `Stateful` y `Stateless`?*  
   **R:** El Stateful (`Route`) conoce la procedencia del estado y el ViewModel; el Stateless (`Screen`) es agnóstico y solo renderiza parámetros primitivos o inmutables.
4. *¿Cómo funciona `VisualTransformation` en el editor de código?*  
   **R:** Transforma visualmente el texto agregando colores de sintaxis con `AnnotatedString` sin modificar la cadena de texto real que se guarda en la base de datos.
5. *¿Por qué el portapapeles no debe tener `.trim()`?*  
   **R:** Porque alteraría la sangría e indentación de los fragmentos de código, rompiendo sintaxis en lenguajes como Python o Bash.

### Capa de ViewModel y Gestión de Estado
6. *¿Por qué el `UiState` se modela como una `sealed interface`?*  
   **R:** Porque representa estados mutuamente excluyentes (`Loading`, `Success`, `Error`) y permite que el compilador verifique que todos los casos se atiendan en la UI sin ramas `else`.
7. *¿Por qué `MutableStateFlow` debe ser privado?*  
   **R:** Para encapsular la mutabilidad dentro del ViewModel, evitando que la vista o clases externas puedan alterar el estado arbitrariamente.
8. *¿Cómo se diferencian los estados continuos de los eventos puntuales?*  
   **R:** El estado continuo reside en `StateFlow` (permanece al rotar pantalla); el evento puntual viaja por `Channel` y se consume una sola vez (`LaunchedEffect`).
9. *¿Qué ocurre si se usa `viewModelScope` después de destruir la pantalla?*  
   **R:** El framework de Android cancela automáticamente el `viewModelScope` y todas sus corrutinas activas, previniendo fugas de memoria (*memory leaks*).
10. *¿Por qué usar `debounce` en el buscador?*  
    **R:** Para pausar la ejecución de consultas a la base de datos hasta que el usuario deje de teclear durante 300 ms, ahorrando operaciones I/O.

### Capa de Repositorio
11. *¿Por qué el repositorio es una interfaz y no una clase directa? (Pregunta trampa)*  
    **R:** Para aplicar Inversión de Dependencias (DIP), permitiendo que la capa de UI dependa de contratos abstractos y facilitando la sustitución por implementaciones mock en tests.
12. *¿Qué significa que el repositorio sea la Única Fuente de Verdad (SSOT)?*  
    **R:** Que todos los datos que consume la aplicación provienen de una única fuente centralizada y coherente (Room), evitando discrepancias entre memorias temporales.
13. *¿Dónde se realiza el mapeo de entidades a modelos de UI?*  
    **R:** En funciones de extensión dentro de la capa UI (`mapper/`), asegurando que las entidades de Room no traspasen la frontera hacia los composables.

### Capa de Room Database
14. *¿Qué pasa si Room se lee sin `Flow`? (Pregunta trampa)*  
    **R:** La UI recibiría una lista estática en un momento dado y no reaccionaría en tiempo real cuando ocurran inserciones, ediciones o eliminaciones en otras pantallas.
15. *¿Por qué las operaciones de escritura son `suspend fun`?*  
    **R:** Porque realizar operaciones de I/O en SQLite bloquea el hilo donde se ejecutan; al ser `suspend`, Room traslada la ejecución al hilo de background (`Dispatchers.IO`).
16. *¿Cómo funciona la eliminación en cascada en DevVault?*  
    **R:** Mediante la clave foránea en `SnippetEntity` vinculada al `id` de `CategoriaEntity` con `onDelete = ForeignKey.CASCADE`.
17. *¿Por qué está prohibido `allowMainThreadQueries()`?*  
    **R:** Porque ejecutar consultas en el Main Thread congela el renderizado a 60 fps y provoca caídas de la app por error ANR (*Application Not Responding*).

### Capa de Inyección de Dependencias (Hilt) y Navegación
18. *¿Qué hace `@AndroidEntryPoint` en `MainActivity`?*  
    **R:** Le permite a Hilt inyectar dependencias y habilitar la provisión de ViewModels scoped a la actividad y a Navigation Compose.
19. *¿Por qué las rutas de navegación son clases `@Serializable`?*  
    **R:** Para garantizar *Type-Safety* en tiempo de compilación y evitar errores tipográficos en strings al navegar y transferir argumentos.
20. *¿Por qué no se debe instanciar `new DevVaultRepositoryImpl()` a mano?*  
    **R:** Porque generaría acoplamiento fuerte, impediría sustituir dependencias y violaría el principio de control de dependencias centralizado de Hilt.
