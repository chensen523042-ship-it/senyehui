import request from '@/utils/request'

export const getPackageList = () => request.get('/api/tenant/package/list')
export const createPackage = (data) => request.post('/api/tenant/package', data)
export const updatePackage = (data) => request.put('/api/tenant/package', data)
export const deletePackage = (id) => request.delete(`/api/tenant/package/${id}`)
