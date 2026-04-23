import request from '@/utils/request'

export const getOverview = () => request.get('/api/statistics/overview')
export const getEventStatus = () => request.get('/api/statistics/event-status')
export const getRegistrationTrend = () => request.get('/api/statistics/registration-trend')
