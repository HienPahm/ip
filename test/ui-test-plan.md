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
