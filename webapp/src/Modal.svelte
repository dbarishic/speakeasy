<script>
  import { createEventDispatcher } from "svelte";

  const dispatch = createEventDispatcher();
  export let progress;
</script>

<style>
  .modal-background {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.4);
    z-index: 9998;
  }

  .modal {
    position: absolute;
    left: 50%;
    top: 50%;
    width: calc(100vw - 4em);
    max-width: 32em;
    max-height: calc(100vh - 4em);
    overflow: auto;
    transform: translate(-50%, -50%);
    padding: 1em;
    border-radius: 0.2em;
    background: white;
    text-align: left;
    box-shadow: 0 7px 8px -4px rgba(0, 0, 0, 0.2),
      0 13px 19px 2px rgba(0, 0, 0, 0.14), 0 5px 24px 4px rgba(0, 0, 0, 0.12);
    z-index: 9999;
  }

  .reject-button {
    background: none;
    font-weight: 400;
    border: 0;
    display: inline-block;
    margin-right: 1.2rem;
    transition: background-color 400ms cubic-bezier(0.25, 0.8, 0.25, 1);
    padding: 0 1rem;
  }

  .reject-button:hover {
    background-color: rgba(158, 158, 158, 0.2);
  }

  .buttons-container {
    display: flex;
    justify-content: right;
    margin-top: 2rem;
  }

  .progress-container {
    align-content: center;
  }

  .confirm-button:active {
    transform: scale(0.98);
  }

  .progress {
    height: 1.5em;
    width: 100%;
    background-color: #c9c9c9;
    position: relative;
  }
  .progress:before {
    content: attr(data-label);
    font-size: 0.8em;
    position: absolute;
    text-align: center;
    top: 5px;
    left: 0;
    right: 0;
  }
  .progress .value {
    background-color: white;
    display: inline-block;
    height: 100%;
  }

  @keyframes lds-rolling {
    0% {
      -webkit-transform: translate(-50%, -50%) rotate(0deg);
      transform: translate(-50%, -50%) rotate(0deg);
    }
    100% {
      -webkit-transform: translate(-50%, -50%) rotate(360deg);
      transform: translate(-50%, -50%) rotate(360deg);
    }
  }
  @-webkit-keyframes lds-rolling {
    0% {
      -webkit-transform: translate(-50%, -50%) rotate(0deg);
      transform: translate(-50%, -50%) rotate(0deg);
    }
    100% {
      -webkit-transform: translate(-50%, -50%) rotate(360deg);
      transform: translate(-50%, -50%) rotate(360deg);
    }
  }

  /* Chrome only */
  @media all and (-webkit-min-device-pixel-ratio: 0) and (min-resolution: 0.001dpcm) {
    .selector:not(*:root),
    .buttons-container {
      float: right;
    }
  }
</style>

<div class="modal-background" on:click={() => dispatch('reject')} />

<div class="modal">
  {#if progress == 0}
    <slot name="header" />
    <slot />
    <div class="buttons-container">
      <button
        class="reject-button uppercase"
        on:click={() => dispatch('reject')}>
        Changed my mind
      </button>

      <button
        autofocus
        class="confirm-button btn btn--primary uppercase"
        on:click={() => dispatch('confirm')}>
        Confirm
      </button>
    </div>
  {:else}
    <slot name="header" />
    <slot />
    <div class="buttons-container">
      <button
        class="reject-button uppercase"
        on:click={() => dispatch('reject')}>
        Changed my mind
      </button>

      <button disabled class="confirm-button btn btn--primary uppercase">
        <span>{progress}%</span>
      </button>
    </div>
  {/if}
</div>
