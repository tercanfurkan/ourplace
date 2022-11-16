<template>
  <li class="media">
    <div class="media-body">
      <div class="media">
        <div class="media-body">
        {{ content }}
        <br/>
        <small class="text-muted">{{ name }} | {{ formatDate(sent) }}</small>
        <hr/>
        </div>
      </div>
    </div>
  </li>
</template>
<script>
  import moment from 'moment'

  export default {
    data: function() {
    },
    props: {
      content: String,
      name: String,
      avatarImageLink: String,
      sent: Date
    },
    methods: {
      formatDate(date) {
        console.log("format date:", date)
        return moment(date).format("DD-MM-YYYY, HH:mm:ss")
      }
    }
  }
</script>