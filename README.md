# 🤖 Virtual Assistant Mod for Minecraft 1.21.1 (Forge)

Мод добавляет в игру летающего виртуального помощника — светящийся куб с глазами, который следует за игроком и помогает выживать.

---

## ✨ Функции

| Функция | Описание |
|---|---|
| 👋 Приветствие | При входе в мир помощник пишет приветствие в чат |
| ⚠️ Сканирование мобов | Каждые 3 секунды проверяет радиус 15 блоков. При обнаружении крипера, зомби, скелета, паука, ведьмы или разбойника — предупреждение в чат |
| ❤️ Здоровье | Если HP падает ниже 3 сердец (6 HP) — экстренное предупреждение в чат |
| 📍 Координаты | Нажатие клавиши **H** показывает текущие координаты X/Y/Z и биом |
| 🦋 Летающий моб | Помощник физически летает рядом с игроком, медленно кружась и покачиваясь |

---

## 🛠️ Сборка и установка

### Требования
- Java 21+
- Minecraft 1.21.1
- Forge 52.0.47+

### Шаги

1. **Скачать Forge MDK** для версии 1.21.1 с [files.minecraftforge.net](https://files.minecraftforge.net)
2. **Настроить рабочую среду:**
   ```bash
   ./gradlew genIntellijRuns   # для IntelliJ IDEA
   ./gradlew genEclipseRuns    # для Eclipse
   ```
3. **Добавить текстуру** помощника (см. ниже)
4. **Собрать мод:**
   ```bash
   ./gradlew build
   ```
5. Готовый `.jar` будет в папке `build/libs/`
6. Скопировать `.jar` в папку `mods/` вашего Minecraft

---

## 🎨 Текстура помощника

Создайте файл `src/main/resources/assets/assistantmod/textures/entity/assistant.png`:
- Размер: **32×16 пикселей**
- Формат: PNG с прозрачностью
- Пример цветов: жёлто-золотые тона с яркими белыми глазами

Простой способ: откройте любой онлайн-редактор пикселей (например [piskelapp.com](https://www.piskelapp.com)) и нарисуйте развёртку куба 6×6 блоков.

---

## 📁 Структура проекта

```
src/main/java/com/assistantmod/
├── AssistantMod.java              — Главный класс мода
├── registry/
│   └── ModEntities.java           — Регистрация сущности помощника
├── entity/
│   ├── AssistantEntity.java       — Логика летающего моба
│   └── AssistantRenderer.java     — Рендер + модель
├── event/
│   ├── AssistantServerEvents.java — Приветствие, скан мобов, здоровье
│   ├── AssistantClientEvents.java — Клавиша H (координаты)
│   ├── ModClientSetup.java        — Регистрация рендера
│   └── ModEntityAttributeEvent.java — Атрибуты сущности
└── client/
    └── KeyBindings.java           — Настройка клавиши H
```

---

## ⚙️ Настройка параметров

Откройте `AssistantServerEvents.java` и измените константы вверху файла:

```java
private static final int SCAN_RADIUS = 15;      // Радиус сканирования (блоки)
private static final float HEALTH_THRESHOLD = 6.0f; // Порог здоровья (6 = 3 сердца)
private static final int SCAN_INTERVAL = 60;    // Интервал сканирования (тики, 20 = 1 сек)
```

---

## 📜 Лицензия

MIT License — используйте свободно.
