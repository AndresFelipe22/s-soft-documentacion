import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell, { tableCellClasses } from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { Button, FormControl, IconButton, InputLabel, Menu, MenuItem, Select, styled, TableFooter, TablePagination } from '@mui/material';
import TablePaginationActions from '@mui/material/TablePagination/TablePaginationActions';
import { useAppDispatch, useAppSelector } from '../../../Redux Toolkit/Store';
import { fetchSellers, selectSellers, updateSellerAccountStatus } from '../../../Redux Toolkit/Seller/sellerSlice';
import { Coupon } from '../../../types/couponTypes';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import { deleteCoupon } from '../../../Redux Toolkit/Admin/AdminCouponSlice';



const StyledTableCell = styled(TableCell)(({ theme }) => ({
    [`&.${tableCellClasses.head}`]: {
        backgroundColor: theme.palette.common.black,
        color: theme.palette.common.white,
    },
    [`&.${tableCellClasses.body}`]: {
        fontSize: 14,
    },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
    '&:nth-of-type(odd)': {
        backgroundColor: theme.palette.action.hover,
    },
    // hide last border
    '&:last-child td, &:last-child th': {
        border: 0,
    },
}));

const accountStatuses = [
    { status: 'ACTIVE', title: 'Activo', description: 'La cuenta está activa y en buen estado' },

    { status: 'PENDING_VERIFICATION', title: 'Pendiente de Verificación', description: 'La cuenta está creada pero aún no verificada' },
    { status: 'SUSPENDED', title: 'Suspendida', description: 'La cuenta está temporalmente suspendida, posiblemente debido a violaciones' },
    { status: 'DEACTIVATED', title: 'Desactivada', description: 'La cuenta está desactivada, el usuario puede haber elegido desactivarla' },
    { status: 'BANNED', title: 'Prohibida', description: 'La cuenta está permanentemente prohibida debido a violaciones graves' },
    { status: 'CLOSED', title: 'Cerrada', description: 'La cuenta está permanentemente cerrada, posiblemente a solicitud del usuario' }
];


export default function CouponTable() {
    const [page, setPage] = React.useState(0);
    const [status, setStatus] = React.useState(accountStatuses[0].status)
    const { sellers, adminCoupon } = useAppSelector(store => store)
    const dispatch = useAppDispatch();

    const handleDeleteCoupon = (id:number) => {
        dispatch(deleteCoupon({ id, jwt: localStorage.getItem("jwt") || "" }))
    }



    return (
        <>
            <div className='pb-5 w-60'>
                <FormControl color='primary' fullWidth>
                    <Select
                        //   labelId="demo-simple-select-label"
                        id="demo-simple-select"
                        value={status}
                        // onChange={handleAccountStatusChange}
                        color='primary'
                        className='text-primary-color'

                    >
                        {accountStatuses.map((status) =>
                            <MenuItem value={status.status}>{status.title}</MenuItem>)}

                    </Select>
                </FormControl>
            </div>

            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 700 }} aria-label="customized table">
                    <TableHead>
                        <TableRow>
                            <StyledTableCell>Código de cupón</StyledTableCell>
                            <StyledTableCell>Fecha de inicio</StyledTableCell>
                            <StyledTableCell>Fecha de finalización</StyledTableCell>
                            <StyledTableCell>Valor mínimo de pedido</StyledTableCell>
                            <StyledTableCell>Descuento %</StyledTableCell>
                            <StyledTableCell align="right">Estado</StyledTableCell>
                            <StyledTableCell align="right">Eliminar</StyledTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {adminCoupon.coupons?.map((coupon: Coupon) => (
                            <StyledTableRow key={coupon.id}>
                                <StyledTableCell component="th" scope="row">
                                    {coupon.code}
                                </StyledTableCell>
                                <StyledTableCell >{coupon.validityStartDate}</StyledTableCell>
                                <StyledTableCell >{coupon.validityEndDate}</StyledTableCell>
                                <StyledTableCell >{coupon.minimumOrderValue}</StyledTableCell>
                                <StyledTableCell >{coupon.discountPercentage}</StyledTableCell>
                                <StyledTableCell align="right">{coupon.active ? "Active" : "Deactive"}</StyledTableCell>

                                <StyledTableCell align="right">
                                    <IconButton onClick={() => handleDeleteCoupon(coupon.id)}>
                                        <DeleteOutlineIcon className='text-red-700 cursor-pointer' />
                                    </IconButton>

                                </StyledTableCell>

                            </StyledTableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </>

    );
}
