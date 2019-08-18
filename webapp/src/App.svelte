<script>
  import Title from "./Title.svelte";
  import TextInput from "./TextInput.svelte";

  export let header;
  export let subheader;
</script>

<style>
  .copyright-info {
    bottom: 0;
    margin: 0 auto;
    left: 0;
    right: 0;
    text-align: center;
    position: absolute;
    font-size: 0.8rem;
    margin-top: 15%;
  }

  :global(.btn) {
    display: inline-block;
    background: transparent;
    color: inherit;
    font: inherit;
    border: 0;
    outline: 0;
    padding: 0;
    transition: all 200ms ease-in;
    cursor: pointer;
  }
  :global(.btn--primary) {
    background: #000;
    color: #fff;
    box-shadow: 0 0 10px 2px rgba(0, 0, 0, 0.1);
    border-radius: 2px;
    padding: 12px 36px;
    font-family: "Overpass", sans-serif;
    font-weight: 900;
  }
  :global(.btn--primary:hover) {
    background: #333;
  }
  :global(.btn--primary:active) {
    background: #333;
    box-shadow: inset 0 0 10px 2px rgba(0, 0, 0, 0.2);
  }
  :global(.btn--inside) {
    margin-left: -96px;
  }

  /* 
  ##Device = Most of the Smartphones Mobiles (Portrait)
  ##Screen = B/w 320px to 479px
*/

  @media (min-width: 320px) and (max-width: 480px) {
    .copyright-info {
      font-size: 10px;
      position: static;
    }
  }
</style>

<Title {header} {subheader} />
<TextInput />

<div class="copyright-info">
  <p>Copyright © 2019 Davor Barishic-Jaman. All rights reserved.</p>
</div>

<link
  href="https://fonts.googleapis.com/css?family=Overpass:100"
  rel="stylesheet" />
