# Dev Notes
## DB migration
- when moving db, need to resync the auto incrementing, if not will have issues with inserting stuff into db with the issue of "duplicate primary key" (e.g. workout id 1 already exists beacuse have 30 workouts, but the new db's current latest index is 1)
  - Solution: run this in the sql editor
    
    ```sql
    SELECT setval(
    'workouts_workout_id_seq',
    (SELECT MAX(workout_id) FROM workouts)
    );
    SELECT setval(
    'workout_exercise_id_seq',
    (SELECT MAX(id) FROM workout_exercise)
    );
    SELECT setval(
    'history_history_id_seq',
    (SELECT MAX(history_id) FROM history)
    );
    SELECT setval(
    'user_achievements_id_seq',
    (SELECT MAX(id) FROM user_achievements)
    );
    ```
    there may be others, take note of this