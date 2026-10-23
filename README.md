# Halgvent: таблица, Атлас и текстуры

Рабочие файлы проекта — всегда **последняя версия**. Дата обновления — в сообщении коммита.

| Файл | Что это |
|---|---|
| [TABLITsA1.xlsx](TABLITsA1.xlsx) | Таблица баланса (ТАБЛИЦА1) |
| [ATLAS_Redaktsiya_I.docx](ATLAS_Redaktsiya_I.docx) | Атлас, редакция I |
| [textures/](textures) | Текстуры: `armor/`, `items/` |

Прямые ссылки на скачивание:
- https://github.com/bynelog/Halgventmod-Neoclassic/raw/halgvent-files/TABLITsA1.xlsx
- https://github.com/bynelog/Halgventmod-Neoclassic/raw/halgvent-files/ATLAS_Redaktsiya_I.docx

## Как устроена эта ветка

- Здесь нет истории: в ветке всегда один коммит, и при каждом обновлении он **заменяется** (force push). Старые версии не хранятся, поэтому репозиторий не растёт.
- Обновляется с компьютера автора, когда он заливает новую версию.
- **Не коммитьте сюда сами** — изменения будут перезаписаны. Нужна правка — напишите автору.
- `git pull` по этой ветке не сработает из-за замены коммита. Чтобы обновить локальную копию:
  `git fetch origin halgvent-files` и `git reset --hard origin/halgvent-files` (на этой ветке).
