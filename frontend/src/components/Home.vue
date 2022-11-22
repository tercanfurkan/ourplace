<template>
  <div
    class="lg:min-h-230 bg-gray-100 flex flex-col justify-center py-12 px-6 lg:px-8"
  >
    <Navbar />
    <Place />
  </div>
</template>

<script>
import Navbar from "./Navbar.vue";
import Place from "./Place.vue";

export default {
  components: {
    Navbar,
    Place,
  },
};
</script>
