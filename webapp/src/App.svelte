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
    position: fixed;
    font-size: 0.8rem;
    margin-top: 15%;
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
