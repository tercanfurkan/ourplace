<template>
  <div class="lg:min-h-230 bg-gray-100 flex flex-col justify-center py-12 px-6 lg:px-8">
      <header-vue />
      <chat-vue />
  </div>
</template>

<script>
import headerVue from './Header.vue'
import chatVue from './Chat.vue';

export default {
  components: {
    chatVue,
    headerVue,
  },
};
</script>
