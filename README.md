# Full plate

## Developer Notes

### Drag and Drop Architecture In Kitchen

Here are some details regarding the usage of the `DragAndDrop` class in this project:

1. Component actors have two methods:
    - `getDragSource()`: Should return a `DragAndDrop.Source` object where source is the actor.
    - `getDropTarget()`: Should return a `DragAndDrop.Target` object where target is the actor.

   These methods are called once the actor is added to a stage.
2. Components should not couple. Use `KitchenEvent` instead and use the `dispatchStageEvent` to
   dispatch the event onto the stage.
3. `DragAndDrop.Source::dragStop` should manage the payload only if drop target was found.
4. `DragAndDrop.Target::drop` should dispatch an `KitchenEvent` and should not mess with the
   `Payload.dragActor`.
