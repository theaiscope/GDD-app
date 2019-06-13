# Application Architecture

## MVP

We use the MVP pattern to enable separation of concerns and define the application layers

### Views

Views describe abstractly what a user or a system can do with 1 particular UI.
Views are an interface that are implemented by an activity (or fragment).
The activity realizes the view.

### Presenter

Presenters mediate between views & the rest of the application.
They depend on a view. They receive and send commands from/to views.
Presenters are independent of Android component

## Dependency Injection

We use DI to be able to test in isolation, to separate concerns, and to enable the MVP pattern.
To make life easier, we implement DI using dagger.