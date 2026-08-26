# UI Test Plan

This file drives the `test-ui` skill. It records how to build/run the
program, and the list of test cases the skill executes.

Each test case is run as its **own fresh program session** (state does not
carry over between test cases). Every test case ends with `bye` so the
program exits cleanly.

## How to run the program

```
javac -d out src/main/java/*.java
java -cp out Will
```

Feed the "Inputs" of a test case to the program's stdin, one command per
line, and compare stdout against "Expected output" (both trimmed of
trailing whitespace per line). Ignore anything the JVM/launcher prints to
stderr (e.g. `JAVA_TOOL_OPTIONS` notices) — only stdout is checked.

The program saves tasks to `./data/will.txt` (created automatically if
the `data/` folder doesn't exist yet) after every command that changes
the task list, and loads it back at startup. Delete the `data/` folder
before **every** test case, not just the ones that check file content
— since startup now depends on it, a stale file left over from the
previous test case would silently change a later test case's starting
task count.

## Test cases

### TC1 — Greet and exit

**Aim:** The chatbot greets the user on startup and exits cleanly on `bye`.

**Inputs:**
```
bye
```

**Expected output:**
```
    ____________________________________________________________
 __        _____ _     _
 \ \      / /_ _| |   | |
  \ \ /\ / / | || |   | |
   \ V  V /  | || |___| |___
    \_/\_/  |___|_____|_____|

     What's up!!! I'm Will.
     How may I assist you?
    ____________________________________________________________
     Seee yaaaa! Meet again soon!
    ____________________________________________________________
```

### TC2 — List when empty

**Aim:** `list` on a fresh session shows the header with no items.

**Inputs:**
```
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
```

### TC3 — Add a Todo

**Aim:** `todo <description>` adds a `[T]` task and reports the new count.

**Inputs:**
```
todo borrow book
bye
```

**Expected output (add confirmation):**
```
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
```

### TC4 — Add a Deadline

**Aim:** `deadline <description> /by <when>` adds a `[D]` task with the
`(by: ...)` suffix.

**Inputs:**
```
deadline return book /by Sunday
bye
```

**Expected output (add confirmation):**
```
     Got it. I've added this task:
       [D][ ] return book (by: Sunday)
     Now you have 1 tasks in the list.
```

### TC5 — Add an Event

**Aim:** `event <description> /from <start> /to <end>` adds an `[E]` task
with the `(from: ... to: ...)` suffix.

**Inputs:**
```
event project meeting /from Mon 2pm /to 4pm
bye
```

**Expected output (add confirmation):**
```
     Got it. I've added this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 1 tasks in the list.
```

### TC6 — List shows all three task types

**Aim:** After adding one of each task type, `list` shows them numbered,
in order, each with its type tag and status icon.

**Inputs:**
```
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

### TC7 — Mark a task as done

**Aim:** `mark <index>` flips the task's status to done and echoes it back.

**Inputs:**
```
todo borrow book
mark 1
bye
```

**Expected output (mark confirmation):**
```
     Amazing Gangie! I've marked this task as done:
       [T][X] borrow book
```

### TC8 — Unmark a task

**Aim:** `unmark <index>` flips a done task back to not-done and echoes it
back.

**Inputs:**
```
todo borrow book
mark 1
unmark 1
bye
```

**Expected output (unmark confirmation):**
```
     OK, I've marked this task as not done yet:
       [T][ ] borrow book
```

### TC9 — Mark/unmark status persists in list

**Aim:** A task marked done stays done (and shows `[X]`) in a later `list`.

**Inputs:**
```
todo borrow book
todo join sports club
mark 2
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[T][X] join sports club
```

### TC10 — Empty todo description

**Aim:** `todo` with no description is rejected with a `WillException`
error message instead of adding a blank task.

**Inputs:**
```
todo
bye
```

**Expected output (error section only):**
```
     OOPS!!! A todo needs a description! Try: todo <what you need to do>
```

### TC11 — Unknown command

**Aim:** An unrecognized command (e.g. `blah`) is rejected with an error
message instead of being silently ignored or crashing.

**Inputs:**
```
blah
bye
```

**Expected output (error section only):**
```
     OOPS!!! I don't recognize that command. Try: todo, deadline, event, list, mark, unmark, delete, or bye.
```

### TC12 — mark with no task number

**Aim:** `mark` with no argument reports a specific error rather than
throwing an unhandled exception.

**Inputs:**
```
mark
bye
```

**Expected output (error section only):**
```
     OOPS!!! Tell me which task number! Try: mark <task number>
```

### TC13 — mark with an out-of-range task number

**Aim:** `mark <n>` where `n` doesn't correspond to an existing task
reports how many tasks actually exist.

**Inputs:**
```
mark 99
bye
```

**Expected output (error section only):**
```
     OOPS!!! Task number 99 doesn't exist. You have 0 task(s) in your list.
```

### TC14 — mark with a non-numeric task number

**Aim:** `mark <not a number>` is rejected with a specific message.

**Inputs:**
```
mark abc
bye
```

**Expected output (error section only):**
```
     OOPS!!! "abc" isn't a valid task number.
```

### TC15 — Deadline missing /by

**Aim:** `deadline <description>` with no `/by` clause is rejected.

**Inputs:**
```
deadline return book
bye
```

**Expected output (error section only):**
```
     OOPS!!! A deadline needs a description and a /by time! Try: deadline <what you need to do> /by <when it's due>
```

### TC16 — Event missing /to

**Aim:** `event <description> /from <start>` with no `/to` clause is
rejected.

**Inputs:**
```
event meeting /from Mon
bye
```

**Expected output (error section only):**
```
     OOPS!!! An event needs a description, a /from time and a /to time! Try: event <what's happening> /from <start> /to <end>
```

## Edge-case / internal-state tests

The tests below interleave valid commands with invalid ones and re-check
`list` afterwards. Their purpose isn't just "does this command error" —
it's "does a rejected command leave the task list/state exactly as it
was", which a plain per-command test can miss (e.g. a bug that increments
the task counter even when validation fails, or that half-applies a
change before throwing).

### TC17 — A rejected command does not change the task count or list

**Aim:** After a valid `todo`, an unrecognized command must not add a
phantom task or otherwise change what `list` shows.

**Inputs:**
```
todo take out trash
blah
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] take out trash
```

### TC18 — mark with task number 0 (just below the valid range)

**Aim:** Task numbers are 1-indexed; `mark 0` must be rejected as
out-of-range, not treated as a valid (off-by-one) index.

**Inputs:**
```
todo take out trash
todo buy milk
mark 0
list
bye
```

**Expected output:**
```
     OOPS!!! Task number 0 doesn't exist. You have 2 task(s) in your list.
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] take out trash
     2.[T][ ] buy milk
```
(Note: both tasks must still show `[ ]` — the rejected `mark 0` must not
have flipped either task's status.)

### TC19 — mark with a negative task number

**Aim:** A negative task number is rejected as out-of-range rather than
wrapping around or crashing.

**Inputs:**
```
todo take out trash
mark -1
list
bye
```

**Expected output:**
```
     OOPS!!! Task number -1 doesn't exist. You have 1 task(s) in your list.
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] take out trash
```

### TC20 — Internal whitespace in a description is preserved

**Aim:** Leading/trailing whitespace around the command is trimmed, but
whitespace the user typed inside the description itself is left alone.

**Inputs:**
```
todo    read   book
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] read   book
```

### TC21 — Deadline with /by but an empty description

**Aim:** `deadline /by <when>` (nothing before `/by`) is rejected with the
"needs a description" message, not treated as a deadline with a blank
description.

**Inputs:**
```
deadline /by Monday
list
bye
```

**Expected output:**
```
     OOPS!!! A deadline needs a description before /by! Try: deadline <what you need to do> /by <when it's due>
    ____________________________________________________________
     Here are the tasks in your list:
```
(Note: `list` afterwards must show no tasks — the rejected deadline must
not have been added.)

### TC22 — Event with /from but an empty description

**Aim:** `event /from <start> /to <end>` (nothing before `/from`) is
rejected with the "needs a description" message.

**Inputs:**
```
event /from Mon /to 5pm
list
bye
```

**Expected output:**
```
     OOPS!!! An event needs a description before /from! Try: event <what's happening> /from <start> /to <end>
    ____________________________________________________________
     Here are the tasks in your list:
```

### TC23 — Unmarking an already-not-done task is idempotent

**Aim:** `unmark` on a task that's already not done should not error —
it's a valid (if redundant) request, and the task must remain `[ ]`.

**Inputs:**
```
todo take out trash
unmark 1
unmark 1
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] take out trash
```

### TC24 — Delete a task

**Aim:** `delete <index>` removes the task at that (1-indexed) position,
echoes it back, and reports the new count.

**Inputs:**
```
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
delete 3
list
bye
```

**Expected output:**
```
     Noted. I've removed this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 2 tasks in the list.
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: June 6th)
```
(Note: the task numbers renumber after the delete — the old "task 2"
`return book` is now "1.", confirming this is a real removal from an
`ArrayList<Task>`, not just blanking a slot.)

### TC25 — delete with no task number

**Aim:** `delete` with no argument reports a specific error.

**Inputs:**
```
delete
bye
```

**Expected output (error section only):**
```
     OOPS!!! Tell me which task number! Try: delete <task number>
```

### TC26 — delete with an out-of-range task number

**Aim:** `delete <n>` where `n` doesn't correspond to an existing task is
rejected and does not remove anything.

**Inputs:**
```
todo read book
delete 99
list
bye
```

**Expected output:**
```
     OOPS!!! Task number 99 doesn't exist. You have 1 task(s) in your list.
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
```

## Persistence tests

### TC27 — Tasks are saved to disk after every change

**Aim:** `./data/will.txt` is created automatically (even if `data/`
doesn't exist yet) and reflects the task list's current state — added
tasks, a marked-done status, and a deletion — after each change.

**Setup:** Delete the `data/` folder (if present) before running this
test case, so it starts from "the folder doesn't exist yet".

**Inputs:**
```
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
delete 2
bye
```

**Expected file content of `data/will.txt` after the session ends:**
```
T | 1 | read book
E | 0 | project meeting | Aug 6th 2pm | 4pm
```
(Note: task 1 is `1` for done since it was marked; task 2, "return
book", is gone since it was deleted; the surviving event's line has 5
`|`-delimited fields: type, done flag, description, from, to.)

### TC28 — A fresh checkout with no data folder still saves correctly

**Aim:** The very first save on a machine that has never run the
program before (no `data/` folder exists at all) must succeed rather
than throwing because the parent folder is missing.

**Setup:** Delete the `data/` folder (if present) before running.

**Inputs:**
```
todo first run test
bye
```

**Expected file content of `data/will.txt` after the session ends:**
```
T | 0 | first run test
```

### TC29 — Tasks are loaded from disk at startup

**Aim:** On startup, the program reads `data/will.txt` and populates the
task list from it (preserving the done/not-done status), before any
command is typed.

**Setup:** Delete the `data/` folder, then create `data/will.txt` with
this exact content before starting the program:
```
T | 1 | read book
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th 2pm | 4pm
```

**Inputs:**
```
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: June 6th)
     3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
```

### TC30 — Corrupted lines are skipped with a warning, valid ones still load

**Aim:** A data file with some unreadable lines (unknown type tag, too
few fields) doesn't stop the whole load — it reports each bad line and
loads everything else.

**Setup:** Delete the `data/` folder, then create `data/will.txt` with
this exact content before starting the program:
```
T | 0 | valid todo
X | 0 | bad type
D | 1 | valid deadline | June 6th
not even close to valid
```

**Inputs:**
```
list
bye
```

**Expected output:**
```
     OOPS!!! Skipping a corrupted line in the data file: "X" isn't a recognized task type.
     OOPS!!! Skipping a corrupted line in the data file: "not even close to valid" doesn't have enough fields.
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] valid todo
     2.[D][X] valid deadline (by: June 6th)
```

## Robustness / edge-case tests (parsing and data integrity)

### TC31 — Leading whitespace on the command line doesn't break parsing

**Aim:** A line like `"   todo read book"` (leading spaces before the
command) must still be recognized as `todo`, not as an empty/unknown
command. Before this fix, `input.split(" ", 2)[0]` on a string with
leading spaces produced an empty first token.

**Inputs:**
```
   todo read book
list
bye
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] read book
```

### TC32 — Blank lines are silently ignored, not treated as errors

**Aim:** Pressing Enter with nothing typed (or only whitespace) should
not produce an "OOPS!!!" — the program should just wait for the next
line.

**Inputs:**
```

   
todo x
bye
```

**Expected output:**
```
    ____________________________________________________________
 __        _____ _     _
 \ \      / /_ _| |   | |
  \ \ /\ / / | || |   | |
   \ V  V /  | || |___| |___
    \_/\_/  |___|_____|_____|

     What's up!!! I'm Will.
     How may I assist you?
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] x
     Now you have 1 tasks in the list.
    ____________________________________________________________
     Seee yaaaa! Meet again soon!
    ____________________________________________________________
```
(Note: no `OOPS!!!` line appears for the two blank inputs before `todo x`.)

### TC33 — Commands are case-insensitive

**Aim:** `TODO`, `LiSt`, `BYE`, etc. should all work the same as their
lowercase form — only the command word's case is normalized, not the
description text itself.

**Inputs:**
```
TODO read book
LiSt
BYE
```

**Expected output (list section only):**
```
     Here are the tasks in your list:
     1.[T][ ] read book
```

### TC34 — Event with /to before /from is rejected, not a crash

**Aim:** Before this fix, `/to` appearing before `/from` in the input
caused a `StringIndexOutOfBoundsException` (an unhandled crash) because
the code assumed `/from` always comes first when slicing substrings.
Now it must be caught and reported as a normal `WillException`.

**Inputs:**
```
event meeting /to 5pm /from 3pm
bye
```

**Expected output (error section only):**
```
     OOPS!!! Your /from time needs to come before /to! Try: event <what's happening> /from <start> /to <end>
```

### TC35 — A "|" character in a description is rejected, not silently corrupted

**Aim:** The save file format is pipe-delimited (`T | 0 | description`).
Before this fix, a description containing `|` would be written to disk
in a way that misparses into extra fields on the next load. It must
now be rejected up front with a clear error instead.

**Inputs:**
```
todo buy milk | bread
bye
```

**Expected output (error section only):**
```
     OOPS!!! Sorry, the description can't contain a "|" character — try rephrasing without it.
```
