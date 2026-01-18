import { createRouter, createWebHistory } from 'vue-router'

import OfCreate from '../views/of/OfCreate.vue' // Nouvelle importation
import OfList from '../views/of/OfList.vue'// Pour lister les OF
import FabricationList from '../views/fabrication/FabricationList.vue'
import FabricationCreate from '../views/fabrication/FabricationCreate.vue'


const routes = [
  { path: '/', redirect: '/home' },
  { path: '/of', name: 'OfList', component: OfList },
  { path: '/of/create', name: 'OfCreate', component: OfCreate },
  {
    path: '/fabrication',
    name: 'FabricationList',
    component: FabricationList
  },
  {
    path: '/fabrication/create',
    name: 'FabricationCreate',
    component: FabricationCreate
  },
  {
    path: '/fabrication/:id/stock',
    name: 'FabricationStock',
    component: () => import('../views/fabrication/FabricationStock.vue'),
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
