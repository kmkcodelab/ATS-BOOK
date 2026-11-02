# Local Data Schema & Offline Assets for ats book

Since ats book is a 100% offline app, all content is stored inside assets/stories.json.

Story Data Structure (JSON Schema):
[
  {
    "id": 1,
    "title": "গল্পের শিরোনাম",
    "category": "Romance",
    "author": "লেখকের নাম",
    "content": "গল্পের পুরো টেক্সট এখানে থাকবে...",
    "isLocked": false,
    "readTime": "5 min"
  }
]

User Preferences Schema (Jetpack DataStore):
- bookmarked_ids: List of story IDs saved by user.
- last_read_story_id: ID of the last opened story.
- font_size_scale: Scale factor for reader text size.
- is_dark_mode: Boolean flag for app theme.