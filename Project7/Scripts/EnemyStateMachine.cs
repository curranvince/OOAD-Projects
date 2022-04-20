using UnityEngine;

public class EnemyStateMachine
{
    public EnemyState[] states;
    public EnemyController controller;
    public EnemyStateID currentState;

    public EnemyStateMachine(EnemyController controller)
    {
        this.controller = controller;
        int numStates = System.Enum.GetNames(typeof(EnemyStateID)).Length;
        states = new EnemyState[numStates];
    }

    public void RegisterState(EnemyState state)
    {
        int index = (int)state.GetID();
        states[index] = state;
    }

    public EnemyState GetState(EnemyStateID stateID)
    {
        int index = (int)stateID;
        return states[index];
    }

    public void Update() => GetState(currentState)?.Update(controller);

    public void ChangeState(EnemyStateID newState)
    {
        GetState(currentState)?.Exit(controller);
        currentState = newState;
        GetState(currentState)?.Enter(controller);
    }
}
